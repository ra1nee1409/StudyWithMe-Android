package com.studywithme.android.feature.payment.data;

import com.studywithme.android.core.network.ApiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Retrofit endpoints for /api/payment/** (PayOS VietQR)
 */
public interface PaymentApi {

    @POST("api/payment/create")
    Call<ApiResponse<Map<String, Object>>> createPayment(@Body Map<String, Object> body);

    @GET("api/payment/status/{orderCode}")
    Call<ApiResponse<Map<String, Object>>> getPaymentStatus(@Path("orderCode") String orderCode);
}
