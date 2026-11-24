package com.studywithme.android.feature.home.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.studywithme.android.di.NetworkModule;

public class HomeRepository {

    private final HomeApi homeApi;

    public HomeRepository(@NonNull Context context) {
        this.homeApi = NetworkModule.createService(context, HomeApi.class);
    }

    @NonNull
    public HomeApi getApi() {
        return homeApi;
    }
}
