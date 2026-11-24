package com.studywithme.android.feature.auth.data;

import com.studywithme.android.core.network.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Retrofit endpoints for /api/auth/**
 */
public interface AuthApi {

    @POST("api/auth/login")
    Call<ApiResponse<AuthDtos.AuthData>> login(@Body AuthDtos.LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<AuthDtos.AuthData>> register(@Body AuthDtos.RegisterRequest request);
}
