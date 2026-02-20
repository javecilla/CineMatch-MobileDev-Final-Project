package com.example.finalprojectandroiddev2.ui.base;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;

/**
 * Base class for all Activities. Enables edge-to-edge display and provides a consistent log tag.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
    }

    /**
     * Apply window insets to a view ID. Call after setContentView with the root layout ID.
     * Use when the activity uses system bars (status/navigation).
     */
    protected void applyEdgeToEdgeInsets(int rootViewId) {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootViewId), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    protected String getLogTag() {
        return Constants.TAG_UI;
    }

    protected void logD(String msg) {
        Logger.d(getLogTag(), msg);
    }

    protected void logE(String msg) {
        Logger.e(getLogTag(), msg);
    }

    protected void logE(String msg, Throwable tr) {
        Logger.e(getLogTag(), msg, tr);
    }
}
