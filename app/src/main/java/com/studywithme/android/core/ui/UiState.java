package com.studywithme.android.core.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Generic UI state wrapper for LiveData in MVVM architecture.
 */
public class UiState<T> {

    public enum Status {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    @NonNull
    private final Status status;
    @Nullable
    private final T data;
    @Nullable
    private final String errorMessage;

    private UiState(@NonNull Status status, @Nullable T data, @Nullable String errorMessage) {
        this.status = status;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    @NonNull
    public static <T> UiState<T> idle() {
        return new UiState<>(Status.IDLE, null, null);
    }

    @NonNull
    public static <T> UiState<T> loading() {
        return new UiState<>(Status.LOADING, null, null);
    }

    @NonNull
    public static <T> UiState<T> success(@Nullable T data) {
        return new UiState<>(Status.SUCCESS, data, null);
    }

    @NonNull
    public static <T> UiState<T> error(@Nullable String errorMessage) {
        return new UiState<>(Status.ERROR, null, errorMessage);
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }
}
