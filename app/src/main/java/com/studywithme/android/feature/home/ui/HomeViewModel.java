package com.studywithme.android.feature.home.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.studywithme.android.feature.home.data.HomeRepository;

public class HomeViewModel extends AndroidViewModel {

    private final HomeRepository repository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.repository = new HomeRepository(application);
    }

    @NonNull
    public HomeRepository getRepository() {
        return repository;
    }
}
