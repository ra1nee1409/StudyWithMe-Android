package com.studywithme.android.feature.course.data;

import com.studywithme.android.core.network.ApiResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit endpoints for /api/public/khoa-hoc and /api/hoc-vien/tien-do
 */
public interface CourseApi {

    @GET("api/public/khoa-hoc")
    Call<ApiResponse<List<Map<String, Object>>>> getCourses();

    @GET("api/public/khoa-hoc/{id}")
    Call<ApiResponse<Map<String, Object>>> getCourseDetail(@Path("id") String courseId);

    @GET("api/hoc-vien/tien-do/{courseId}")
    Call<ApiResponse<Map<String, Object>>> getCourseProgress(@Path("courseId") String courseId);
}
