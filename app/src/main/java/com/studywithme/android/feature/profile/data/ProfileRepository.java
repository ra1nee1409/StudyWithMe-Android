package com.studywithme.android.feature.profile.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.studywithme.android.di.NetworkModule;

public class ProfileRepository {

    private final ProfileApi profileApi;

    public ProfileRepository(@NonNull Context context) {
        this.profileApi = NetworkModule.createService(context, ProfileApi.class);
    }

    @NonNull
    public ProfileApi getApi() {
        return profileApi;
    }
}
