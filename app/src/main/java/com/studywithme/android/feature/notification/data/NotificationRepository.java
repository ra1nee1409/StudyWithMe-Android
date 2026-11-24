package com.studywithme.android.feature.notification.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.studywithme.android.di.NetworkModule;

public class NotificationRepository {

    private final NotificationApi notificationApi;

    public NotificationRepository(@NonNull Context context) {
        this.notificationApi = NetworkModule.createService(context, NotificationApi.class);
    }

    @NonNull
    public NotificationApi getApi() {
        return notificationApi;
    }
}
