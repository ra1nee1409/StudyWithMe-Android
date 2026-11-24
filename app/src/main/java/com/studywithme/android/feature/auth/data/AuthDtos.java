package com.studywithme.android.feature.auth.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Request/Response DTOs for Authentication endpoints (/api/auth/**).
 */
public final class AuthDtos {

    private AuthDtos() {
    }

    public static class LoginRequest {
        @SerializedName("email")
        public final String email;

        @SerializedName("password")
        public final String password;

        public LoginRequest(@NonNull String email, @NonNull String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class RegisterRequest {
        @SerializedName("fullName")
        public final String fullName;

        @SerializedName("email")
        public final String email;

        @SerializedName("password")
        public final String password;

        public RegisterRequest(
                @NonNull String fullName,
                @NonNull String email,
                @NonNull String password
        ) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
        }
    }

    public static class AuthData {
        @SerializedName("token")
        @Nullable
        public String token;

        @SerializedName("userId")
        @Nullable
        public String userId;

        @SerializedName("role")
        @Nullable
        public String role;
    }
}
