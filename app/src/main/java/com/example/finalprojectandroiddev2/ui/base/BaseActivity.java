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
            // Convert 24dp to px — this is the layout's base padding defined in XML.
            // We ADD it to the system bar insets because setPadding() replaces all padding;
            // without this, horizontal padding (which has 0 system insets) would be wiped.
            int dp24 = Math.round(24 * v.getContext().getResources().getDisplayMetrics().density);
            v.setPadding(
                    systemBars.left  + dp24,
                    systemBars.top   + dp24,
                    systemBars.right + dp24,
                    systemBars.bottom + dp24
            );
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
