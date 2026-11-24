package com.studywithme.android.core.network;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper matching Spring Boot's ApiResponse<T> JSON payload.
 */
public class ApiResponse<T> {

    @SerializedName("success")
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    @Nullable
    private String message;

    @SerializedName("data")
    @Nullable
    private T data;

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public T getData() {
        return data;
    }
}
