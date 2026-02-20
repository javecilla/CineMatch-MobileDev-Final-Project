package com.example.finalprojectandroiddev2.ui.splash;

import android.os.Bundle;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;

/**
 * Splash screen with app logo and loading indicator.
 * Navigation logic will be added in a later phase.
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        applyEdgeToEdgeInsets(R.id.container_splash);
    }
}
