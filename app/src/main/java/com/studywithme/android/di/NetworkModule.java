package com.studywithme.android.di;

import android.content.Context;

import androidx.annotation.NonNull;

import com.studywithme.android.BuildConfig;
import com.studywithme.android.core.network.AuthInterceptor;
import com.studywithme.android.core.network.StompChatClient;
import com.studywithme.android.core.storage.TokenManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Central provider for Retrofit, OkHttpClient, TokenManager, and StompChatClient.
 */
public final class NetworkModule {

    private static volatile TokenManager tokenManager;
    private static volatile OkHttpClient okHttpClient;
    private static volatile Retrofit retrofit;
    private static volatile StompChatClient stompChatClient;

    private NetworkModule() {
    }

    public static void init(@NonNull Context context) {
        Context appContext = context.getApplicationContext();
        if (tokenManager == null) {
            synchronized (NetworkModule.class) {
                if (tokenManager == null) {
                    tokenManager = new TokenManager(appContext);
                }
            }
        }
    }

    @NonNull
    public static TokenManager provideTokenManager(@NonNull Context context) {
        init(context);
        return tokenManager;
    }

    @NonNull
    public static OkHttpClient provideOkHttpClient(@NonNull Context context) {
        init(context);
        if (okHttpClient == null) {
            synchronized (NetworkModule.class) {
                if (okHttpClient == null) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(
                            BuildConfig.DEBUG
                                    ? HttpLoggingInterceptor.Level.BODY
                                    : HttpLoggingInterceptor.Level.NONE
                    );

                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .addInterceptor(new AuthInterceptor(tokenManager))
                            .addInterceptor(loggingInterceptor)
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    @NonNull
    public static Retrofit provideRetrofit(@NonNull Context context) {
        if (retrofit == null) {
            synchronized (NetworkModule.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BuildConfig.API_BASE_URL)
                            .client(provideOkHttpClient(context))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    @NonNull
    public static <T> T createService(@NonNull Context context, @NonNull Class<T> serviceClass) {
        return provideRetrofit(context).create(serviceClass);
    }

    @NonNull
    public static StompChatClient provideStompChatClient(@NonNull Context context) {
        if (stompChatClient == null) {
            synchronized (NetworkModule.class) {
                if (stompChatClient == null) {
                    stompChatClient = new StompChatClient(
                            provideOkHttpClient(context),
                            provideTokenManager(context)
                    );
                }
            }
        }
        return stompChatClient;
    }
}
