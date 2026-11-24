package com.studywithme.android.feature.payment.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.studywithme.android.di.NetworkModule;

public class PaymentRepository {

    private final PaymentApi paymentApi;

    public PaymentRepository(@NonNull Context context) {
        this.paymentApi = NetworkModule.createService(context, PaymentApi.class);
    }

    @NonNull
    public PaymentApi getApi() {
        return paymentApi;
    }
}
