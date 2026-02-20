package com.example.finalprojectandroiddev2.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.auth.LoginActivity;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;

/**
 * Splash screen with app logo and loading indicator.
 * Navigates to LoginActivity after a brief delay.
 */
public class SplashActivity extends BaseActivity {

    private static final long SPLASH_DELAY_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        applyEdgeToEdgeInsets(R.id.container_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, SPLASH_DELAY_MS);
    }
}
