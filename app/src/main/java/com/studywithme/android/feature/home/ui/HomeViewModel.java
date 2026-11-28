package com.studywithme.android.feature.home.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.studywithme.android.feature.home.data.HomeModels;
import com.studywithme.android.feature.home.data.HomeRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final HomeRepository repository;
    private final MutableLiveData<HomeModels.HomeUiData> homeData =
            new MutableLiveData<>(HomeModels.defaultUiData());
    private final MutableLiveData<List<HomeModels.HomeTutorItem>> visibleTutors =
            new MutableLiveData<>(HomeModels.defaultTutors());
    private final MutableLiveData<String> selectedTag = new MutableLiveData<>("Tất cả");

    private String currentQuery = "";
    private String currentTag = "Tất cả";

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.repository = new HomeRepository(application);
        refresh();
    }

    @NonNull
    public HomeRepository getRepository() {
        return repository;
    }

    @NonNull
    public LiveData<HomeModels.HomeUiData> getHomeData() {
        return homeData;
    }

    @NonNull
    public LiveData<List<HomeModels.HomeTutorItem>> getVisibleTutors() {
        return visibleTutors;
    }

    @NonNull
    public LiveData<String> getSelectedTag() {
        return selectedTag;
    }

    public void refresh() {
        repository.loadHomeData(data -> {
            homeData.postValue(data);
            visibleTutors.postValue(HomeModels.filterTutors(data.tutors, currentQuery, currentTag));
        });
    }

    public void setSearchQuery(@Nullable String query) {
        currentQuery = query == null ? "" : query;
        applyFilters();
    }

    public void selectTag(@NonNull String tag) {
        currentTag = tag;
        selectedTag.setValue(tag);
        applyFilters();
    }

    private void applyFilters() {
        HomeModels.HomeUiData data = homeData.getValue();
        List<HomeModels.HomeTutorItem> source = data != null ? data.tutors : HomeModels.defaultTutors();
        visibleTutors.setValue(HomeModels.filterTutors(source, currentQuery, currentTag));
    }
}
