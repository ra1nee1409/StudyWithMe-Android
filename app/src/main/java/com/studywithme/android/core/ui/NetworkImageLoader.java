package com.studywithme.android.core.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.studywithme.android.BuildConfig;
import com.studywithme.android.di.NetworkModule;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Lightweight image loader backed by the app's shared OkHttpClient and an in-memory LruCache.
 * Resolves relative web paths against BuildConfig.API_BASE_URL.
 */
public final class NetworkImageLoader {

    private static final int CACHE_SIZE_KB = 8 * 1024;
    private static volatile LruCache<String, Bitmap> memoryCache;
    private static volatile Handler mainHandler;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(3);

    private NetworkImageLoader() {
    }

    @NonNull
    private static LruCache<String, Bitmap> getCache() {
        if (memoryCache == null) {
            synchronized (NetworkImageLoader.class) {
                if (memoryCache == null) {
                    memoryCache = new LruCache<>(CACHE_SIZE_KB) {
                        @Override
                        protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                            return Math.max(1, value.getByteCount() / 1024);
                        }
                    };
                }
            }
        }
        return memoryCache;
    }

    @NonNull
    private static Handler getMainHandler() {
        if (mainHandler == null) {
            synchronized (NetworkImageLoader.class) {
                if (mainHandler == null) {
                    mainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mainHandler;
    }

    @Nullable
    public static String resolveUrl(@NonNull String baseUrl, @Nullable String rawUrl) {
        if (rawUrl == null) {
            return null;
        }
        String trimmed = rawUrl.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        String normalizedPath = trimmed.startsWith("/") ? trimmed.substring(1) : trimmed;
        return normalizedBase + normalizedPath;
    }

    public static void load(
            @NonNull ImageView imageView,
            @Nullable String rawUrl,
            @DrawableRes int placeholderRes
    ) {
        String resolvedUrl = resolveUrl(BuildConfig.API_BASE_URL, rawUrl);
        imageView.setTag(resolvedUrl);

        if (resolvedUrl == null) {
            imageView.setImageResource(placeholderRes);
            return;
        }

        LruCache<String, Bitmap> cache = getCache();
        Bitmap cached = cache.get(resolvedUrl);
        if (cached != null && !cached.isRecycled()) {
            imageView.setImageBitmap(cached);
            return;
        }

        imageView.setImageResource(placeholderRes);
        Context appContext = imageView.getContext().getApplicationContext();
        OkHttpClient client = NetworkModule.provideOkHttpClient(appContext);
        Handler handler = getMainHandler();

        // shortcut: no disk cache, upgrade to Glide/Coil when gallery/media screens grow
        EXECUTOR.execute(() -> {
            Bitmap decoded = fetchBitmap(client, resolvedUrl);
            if (decoded != null) {
                cache.put(resolvedUrl, decoded);
                handler.post(() -> {
                    Object currentTag = imageView.getTag();
                    if (resolvedUrl.equals(currentTag)) {
                        imageView.setImageBitmap(decoded);
                    }
                });
            }
        });
    }

    @Nullable
    private static Bitmap fetchBitmap(@NonNull OkHttpClient client, @NonNull String url) {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            try (InputStream is = body.byteStream()) {
                return BitmapFactory.decodeStream(is);
            }
        } catch (Exception ignored) {
            return null;
        }
    }
}
