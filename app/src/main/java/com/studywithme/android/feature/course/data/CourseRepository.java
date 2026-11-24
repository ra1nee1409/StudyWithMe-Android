package com.studywithme.android.feature.course.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.studywithme.android.di.NetworkModule;

public class CourseRepository {

    private final CourseApi courseApi;

    public CourseRepository(@NonNull Context context) {
        this.courseApi = NetworkModule.createService(context, CourseApi.class);
    }

    @NonNull
    public CourseApi getApi() {
        return courseApi;
    }
}
