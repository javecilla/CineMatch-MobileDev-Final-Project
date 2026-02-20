package com.example.finalprojectandroiddev2.utils;

import android.text.TextUtils;

import androidx.annotation.Nullable;

/**
 * General-purpose utility methods.
 */
public final class Utils {

    private Utils() {}

    /**
     * Returns true if the string is null or empty (after trimming).
     */
    public static boolean isBlank(@Nullable String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Returns true if the string has content (non-null, non-empty after trim).
     */
    public static boolean isNotBlank(@Nullable String s) {
        return !isBlank(s);
    }

    /**
     * Returns the string or a default value if blank.
     */
    public static String orDefault(@Nullable String s, String defaultValue) {
        return isNotBlank(s) ? s.trim() : defaultValue;
    }

    /**
     * Safely parses an int, returns defaultValue on failure.
     */
    public static int parseInt(@Nullable String s, int defaultValue) {
        if (TextUtils.isEmpty(s)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
