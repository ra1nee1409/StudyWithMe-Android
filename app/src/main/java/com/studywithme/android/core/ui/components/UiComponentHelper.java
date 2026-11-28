package com.studywithme.android.core.ui.components;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

/**
 * Shared XML UI helpers: loading/error banners, safe navigation, keyboard, dialer, and snackbar.
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

    public static void navigateSafely(@Nullable View root, int destinationOrActionId, @Nullable Bundle args) {
        if (root == null) {
            return;
        }
        try {
            Navigation.findNavController(root).navigate(destinationOrActionId, args);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
            // Guard against double taps during fragment transition.
        }
    }

    public static void hideKeyboard(@NonNull View anchor) {
        InputMethodManager imm = (InputMethodManager) anchor.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(anchor.getWindowToken(), 0);
        }
        anchor.clearFocus();
    }

    public static void openDialer(@NonNull Context context, @NonNull String telUri) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(telUri));
        if (dialIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(dialIntent);
        }
    }

    public static void showToastMessage(@Nullable View root, @NonNull String message) {
        if (root != null) {
            Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
