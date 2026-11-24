package com.studywithme.android.feature.notification.data;

import com.studywithme.android.core.network.ApiResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Retrofit endpoints for /api/notifications/**
 */
public interface NotificationApi {

    @GET("api/notifications")
    Call<ApiResponse<List<Map<String, Object>>>> getNotifications();

    @PUT("api/notifications/{id}/read")
    Call<ApiResponse<Void>> markAsRead(@Path("id") String notificationId);
}
