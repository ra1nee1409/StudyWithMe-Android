package com.studywithme.android.feature.home.data;

import com.studywithme.android.core.network.ApiResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit endpoints matching StudyWithMe Spring Boot PublicApiController & ThongBaoApiController.
 */
public interface HomeApi {

    @GET("api/public/banners")
    Call<ApiResponse<List<HomeModels.BannerDto>>> getBanners();

    @GET("api/public/khoa-hoc/noi-bat")
    Call<ApiResponse<List<Map<String, Object>>>> getFeaturedCourses();

    @GET("api/public/danh-muc/noi-bat")
    Call<ApiResponse<List<HomeModels.CategoryDto>>> getFeaturedCategories();

    @GET("api/public/giang-vien/tieu-bieu")
    Call<ApiResponse<List<HomeModels.FeaturedTutorDto>>> getFeaturedTutors();

    @GET("api/public/giang-vien/{id}/lop-hoc")
    Call<ApiResponse<List<HomeModels.TutorClassDto>>> getTutorClasses(@Path("id") long tutorId);

    @GET("api/notifications/unread-count")
    Call<ApiResponse<Map<String, Long>>> getUnreadNotificationCount();
}
