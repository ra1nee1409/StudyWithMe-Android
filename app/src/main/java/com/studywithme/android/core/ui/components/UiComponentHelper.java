package com.studywithme.android.core.ui.components;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Shared XML UI component helpers (LoadingView, ErrorBanner visibility & state updates).
 */
public final class UiComponentHelper {

    private UiComponentHelper() {
    }

    public static void setLoading(@NonNull View loadingView, boolean isLoading) {
        loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    public static void showErrorBanner(
            @NonNull View errorBannerContainer,
            @NonNull TextView errorTextView,
            @Nullable String message
    ) {
        if (message == null || message.trim().isEmpty()) {
            errorBannerContainer.setVisibility(View.GONE);
        } else {
            errorTextView.setText(message);
            errorBannerContainer.setVisibility(View.VISIBLE);
        }
    }
}
