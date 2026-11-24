package com.studywithme.android.feature.profile.data;

import com.studywithme.android.core.network.ApiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

/**
 * Retrofit endpoints for /api/profile/**
 */
public interface ProfileApi {

    @GET("api/profile")
    Call<ApiResponse<Map<String, Object>>> getProfile();

    @PUT("api/profile")
    Call<ApiResponse<Map<String, Object>>> updateProfile(@Body Map<String, Object> body);

    @PUT("api/profile/change-password")
    Call<ApiResponse<Void>> changePassword(@Body Map<String, Object> body);
}
