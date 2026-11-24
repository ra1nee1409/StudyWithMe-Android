package com.studywithme.android.core.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Local storage manager for JWT token, userId, and role using SharedPreferences.
 */
public class TokenManager {

    private static final String PREF_NAME = "studywithme_auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "user_role";

    private final SharedPreferences sharedPreferences;

    public TokenManager(@NonNull Context context) {
        this.sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(@NonNull String accessToken, @Nullable String userId, @Nullable String role) {
        sharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_ROLE, role)
                .apply();
    }

    @Nullable
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    @Nullable
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    @Nullable
    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, null);
    }

    public boolean isLoggedIn() {
        String token = getAccessToken();
        return token != null && !token.trim().isEmpty();
    }

    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }
}
