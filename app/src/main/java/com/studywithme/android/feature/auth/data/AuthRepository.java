package com.studywithme.android.feature.auth.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.studywithme.android.core.network.ApiResponse;
import com.studywithme.android.core.storage.TokenManager;
import com.studywithme.android.di.NetworkModule;

import retrofit2.Call;

public class AuthRepository {

    private final AuthApi authApi;
    private final TokenManager tokenManager;

    public AuthRepository(@NonNull Context context) {
        this.authApi = NetworkModule.createService(context, AuthApi.class);
        this.tokenManager = NetworkModule.provideTokenManager(context);
    }

    @NonNull
    public Call<ApiResponse<AuthDtos.AuthData>> login(@NonNull String email, @NonNull String password) {
        return authApi.login(new AuthDtos.LoginRequest(email, password));
    }

    @NonNull
    public Call<ApiResponse<AuthDtos.AuthData>> register(
            @NonNull String fullName,
            @NonNull String email,
            @NonNull String password
    ) {
        return authApi.register(new AuthDtos.RegisterRequest(fullName, email, password));
    }

    public void saveSession(@NonNull AuthDtos.AuthData data) {
        if (data.token != null && !data.token.isEmpty()) {
            tokenManager.saveSession(data.token, data.userId, data.role);
        }
    }

    public void logout() {
        tokenManager.clearSession();
    }
}
