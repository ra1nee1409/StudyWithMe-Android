package com.studywithme.android.feature.home.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.studywithme.android.core.network.ApiResponse;
import com.studywithme.android.core.storage.TokenManager;
import com.studywithme.android.di.NetworkModule;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Loads Home data from StudyWithMe web backend public APIs with graceful fallback when offline.
 */
public class HomeRepository {

    public interface HomeDataCallback {
        void onUpdated(@NonNull HomeModels.HomeUiData data);
    }

    private static class MutableSnapshot {
        @Nullable
        String bannerText = null;
        @NonNull
        List<HomeModels.HomeCategoryItem> categories = HomeModels.defaultCategories();
        @NonNull
        List<HomeModels.HomeTutorItem> tutors = HomeModels.defaultTutors();
        long unreadCount = 1L;
        boolean syncedAny = false;

        synchronized HomeModels.HomeUiData toUiData() {
            return new HomeModels.HomeUiData(
                    bannerText,
                    categories,
                    tutors,
                    unreadCount,
                    syncedAny
            );
        }
    }

    private final HomeApi homeApi;
    private final TokenManager tokenManager;

    public HomeRepository(@NonNull Context context) {
        this.homeApi = NetworkModule.createService(context, HomeApi.class);
        this.tokenManager = NetworkModule.provideTokenManager(context);
    }

    @NonNull
    public HomeApi getApi() {
        return homeApi;
    }

    public void loadHomeData(@NonNull HomeDataCallback callback) {
        MutableSnapshot state = new MutableSnapshot();

        homeApi.getBanners().enqueue(new Callback<ApiResponse<List<HomeModels.BannerDto>>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<List<HomeModels.BannerDto>>> call,
                    @NonNull Response<ApiResponse<List<HomeModels.BannerDto>>> response
            ) {
                List<HomeModels.BannerDto> list = extractData(response);
                if (list != null && !list.isEmpty()) {
                    HomeModels.BannerDto first = list.get(0);
                    if (first != null && first.tieuDe != null && !first.tieuDe.trim().isEmpty()) {
                        synchronized (state) {
                            state.bannerText = first.tieuDe.trim();
                            state.syncedAny = true;
                        }
                        callback.onUpdated(state.toUiData());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<HomeModels.BannerDto>>> call, @NonNull Throwable t) {
                // Keep default hero banner when web API is unreachable.
            }
        });

        homeApi.getFeaturedCategories().enqueue(new Callback<ApiResponse<List<HomeModels.CategoryDto>>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<List<HomeModels.CategoryDto>>> call,
                    @NonNull Response<ApiResponse<List<HomeModels.CategoryDto>>> response
            ) {
                List<HomeModels.CategoryDto> list = extractData(response);
                if (list != null && !list.isEmpty()) {
                    synchronized (state) {
                        state.categories = HomeModels.mapCategoriesFromApi(list);
                        state.syncedAny = true;
                    }
                    callback.onUpdated(state.toUiData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<HomeModels.CategoryDto>>> call, @NonNull Throwable t) {
                // Keep default 4 grade/program tiles when offline.
            }
        });

        homeApi.getFeaturedTutors().enqueue(new Callback<ApiResponse<List<HomeModels.FeaturedTutorDto>>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<List<HomeModels.FeaturedTutorDto>>> call,
                    @NonNull Response<ApiResponse<List<HomeModels.FeaturedTutorDto>>> response
            ) {
                List<HomeModels.FeaturedTutorDto> list = extractData(response);
                if (list == null || list.isEmpty()) {
                    return;
                }
                synchronized (state) {
                    state.syncedAny = true;
                    state.tutors = HomeModels.mapTutorsFromApi(list, Collections.emptyMap());
                }
                callback.onUpdated(state.toUiData());
                fetchTutorPrices(list, fees -> {
                    synchronized (state) {
                        state.tutors = HomeModels.mapTutorsFromApi(list, fees);
                    }
                    callback.onUpdated(state.toUiData());
                });
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<HomeModels.FeaturedTutorDto>>> call, @NonNull Throwable t) {
                // Keep default featured tutors when offline.
            }
        });

        if (tokenManager.isLoggedIn()) {
            homeApi.getUnreadNotificationCount().enqueue(new Callback<ApiResponse<Map<String, Long>>>() {
                @Override
                public void onResponse(
                        @NonNull Call<ApiResponse<Map<String, Long>>> call,
                        @NonNull Response<ApiResponse<Map<String, Long>>> response
                ) {
                    Map<String, Long> data = extractData(response);
                    if (data != null && data.containsKey("count") && data.get("count") != null) {
                        synchronized (state) {
                            state.unreadCount = data.get("count");
                        }
                        callback.onUpdated(state.toUiData());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<Map<String, Long>>> call, @NonNull Throwable t) {
                    // Ignore unread count failure.
                }
            });
        }
    }

    private interface FeesCallback {
        void onComplete(@NonNull Map<Long, BigDecimal> lowestFees);
    }

    private void fetchTutorPrices(
            @NonNull List<HomeModels.FeaturedTutorDto> tutors,
            @NonNull FeesCallback callback
    ) {
        Map<Long, BigDecimal> lowestFees = new ConcurrentHashMap<>();
        AtomicInteger remaining = new AtomicInteger(tutors.size());

        for (HomeModels.FeaturedTutorDto tutor : tutors) {
            if (tutor == null || tutor.id == null) {
                if (remaining.decrementAndGet() == 0) {
                    callback.onComplete(lowestFees);
                }
                continue;
            }
            long tutorId = tutor.id;
            homeApi.getTutorClasses(tutorId).enqueue(new Callback<ApiResponse<List<HomeModels.TutorClassDto>>>() {
                @Override
                public void onResponse(
                        @NonNull Call<ApiResponse<List<HomeModels.TutorClassDto>>> call,
                        @NonNull Response<ApiResponse<List<HomeModels.TutorClassDto>>> response
                ) {
                    List<HomeModels.TutorClassDto> classes = extractData(response);
                    if (classes != null) {
                        BigDecimal minFee = null;
                        for (HomeModels.TutorClassDto cls : classes) {
                            if (cls != null && cls.hocPhiMoiHocVien != null
                                    && cls.hocPhiMoiHocVien.compareTo(BigDecimal.ZERO) > 0) {
                                if (minFee == null || cls.hocPhiMoiHocVien.compareTo(minFee) < 0) {
                                    minFee = cls.hocPhiMoiHocVien;
                                }
                            }
                        }
                        if (minFee != null) {
                            lowestFees.put(tutorId, minFee);
                        }
                    }
                    if (remaining.decrementAndGet() == 0 && !lowestFees.isEmpty()) {
                        callback.onComplete(lowestFees);
                    }
                }

                @Override
                public void onFailure(
                        @NonNull Call<ApiResponse<List<HomeModels.TutorClassDto>>> call,
                        @NonNull Throwable t
                ) {
                    if (remaining.decrementAndGet() == 0 && !lowestFees.isEmpty()) {
                        callback.onComplete(lowestFees);
                    }
                }
            });
        }
    }

    @Nullable
    private static <T> T extractData(@NonNull Response<ApiResponse<T>> response) {
        if (!response.isSuccessful()) {
            return null;
        }
        ApiResponse<T> body = response.body();
        if (body == null || !body.isSuccess()) {
            return null;
        }
        return body.getData();
    }
}
