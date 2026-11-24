package com.studywithme.android.feature.home.data;

import com.studywithme.android.core.network.ApiResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit endpoints for /api/public/banners, noi-bat, danh-muc, tieu-bieu
 */
public interface HomeApi {

    @GET("api/public/banners")
    Call<ApiResponse<List<Map<String, Object>>>> getBanners();

    @GET("api/public/khoa-hoc/noi-bat")
    Call<ApiResponse<List<Map<String, Object>>>> getFeaturedCourses();

    @GET("api/public/danh-muc")
    Call<ApiResponse<List<Map<String, Object>>>> getCategories();

    @GET("api/public/giang-vien/tieu-bieu")
    Call<ApiResponse<List<Map<String, Object>>>> getFeaturedTutors();
}
