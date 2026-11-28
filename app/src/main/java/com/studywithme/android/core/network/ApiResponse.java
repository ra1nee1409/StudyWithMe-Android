package com.studywithme.android.core.network;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper matching Spring Boot's ApiResponse<T> JSON payload.
 */
public class ApiResponse<T> {

    @SerializedName(value = "success", alternate = {"thanhCong"})
    private boolean success;

    @SerializedName("code")
    private int code;

    @SerializedName(value = "message", alternate = {"thongBao"})
    @Nullable
    private String message;

    @SerializedName(value = "data", alternate = {"duLieu"})
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
