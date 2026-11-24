package com.studywithme.android.feature.auth.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.studywithme.android.core.network.ApiResponse;
import com.studywithme.android.core.ui.UiState;
import com.studywithme.android.feature.auth.data.AuthDtos;
import com.studywithme.android.feature.auth.data.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<UiState<AuthDtos.AuthData>> _authState =
            new MutableLiveData<>(UiState.idle());
    public final LiveData<UiState<AuthDtos.AuthData>> authState = _authState;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AuthRepository(application);
    }

    public void login(@NonNull String email, @NonNull String password) {
        _authState.setValue(UiState.loading());
        repository.login(email, password).enqueue(new Callback<ApiResponse<AuthDtos.AuthData>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<AuthDtos.AuthData>> call,
                    @NonNull Response<ApiResponse<AuthDtos.AuthData>> response
            ) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    AuthDtos.AuthData data = response.body().getData();
                    repository.saveSession(data);
                    _authState.setValue(UiState.success(data));
                } else {
                    _authState.setValue(UiState.error("Đăng nhập thất bại (HTTP " + response.code() + ")"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AuthDtos.AuthData>> call, @NonNull Throwable t) {
                _authState.setValue(UiState.error(t.getMessage()));
            }
        });
    }
}
