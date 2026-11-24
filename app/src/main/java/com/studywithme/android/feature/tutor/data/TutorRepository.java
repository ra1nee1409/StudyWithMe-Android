package com.studywithme.android.feature.tutor.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.studywithme.android.di.NetworkModule;

public class TutorRepository {

    private final TutorApi tutorApi;

    public TutorRepository(@NonNull Context context) {
        this.tutorApi = NetworkModule.createService(context, TutorApi.class);
    }

    @NonNull
    public TutorApi getApi() {
        return tutorApi;
    }
}
