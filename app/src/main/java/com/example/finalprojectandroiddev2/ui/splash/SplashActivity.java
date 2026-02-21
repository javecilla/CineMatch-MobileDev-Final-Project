package com.example.finalprojectandroiddev2.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.repository.AuthRepository;
import com.example.finalprojectandroiddev2.ui.auth.LoginActivity;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;

/**
 * Splash screen with app logo and loading indicator.
 * On launch, checks Firebase auth state: if authenticated → HomeActivity; if not → LoginActivity.
 */
public class SplashActivity extends BaseActivity {

    private static final long SPLASH_DELAY_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        applyEdgeToEdgeInsets(R.id.container_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::navigateByAuthState, SPLASH_DELAY_MS);
    }

    /**
     * Checks Firebase auth state and navigates to Home if authenticated, Login otherwise.
     * Uses clear-task flags so back from Login/Home does not return to Splash.
     */
    private void navigateByAuthState() {
        AuthRepository authRepository = AuthRepository.getInstance();
        boolean isAuthenticated = authRepository.getCurrentUser() != null;

        if (isAuthenticated) {
            Logger.d(Constants.TAG_AUTH, "Splash: user authenticated, navigating to Home");
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Logger.d(Constants.TAG_AUTH, "Splash: user not authenticated, navigating to Login");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
    }
}
