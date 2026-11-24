package com.studywithme.android.feature.tutor.data;

import com.studywithme.android.core.network.ApiResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit endpoints for /api/public/giang-vien and /api/hoc-vien/dat-lich-1k1
 */
public interface TutorApi {

    @GET("api/public/giang-vien")
    Call<ApiResponse<List<Map<String, Object>>>> getTutors();

    @GET("api/public/giang-vien/{id}")
    Call<ApiResponse<Map<String, Object>>> getTutorDetail(@Path("id") String tutorId);

    @POST("api/hoc-vien/dat-lich-1k1")
    Call<ApiResponse<Map<String, Object>>> createBooking(@Body Map<String, Object> body);
}
