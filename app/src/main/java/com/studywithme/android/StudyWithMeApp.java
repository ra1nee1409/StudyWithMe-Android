package com.studywithme.android;

import android.app.Application;

import com.studywithme.android.di.NetworkModule;

public class StudyWithMeApp extends Application {

    private static StudyWithMeApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        NetworkModule.init(this);
    }

    public static StudyWithMeApp getInstance() {
        return instance;
    }
}
