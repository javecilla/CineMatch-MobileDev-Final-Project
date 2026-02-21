package com.example.finalprojectandroiddev2.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.repository.AuthRepository;
import com.example.finalprojectandroiddev2.data.repository.UserRepository;
import com.example.finalprojectandroiddev2.model.UserProfile;
import com.example.finalprojectandroiddev2.ui.auth.LoginActivity;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.onboarding.OnboardingActivity;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;

/**
 * Splash screen with app logo and loading indicator.
 * On launch: if not authenticated → Login; if authenticated, checks profile → Onboarding (no profile) or Home.
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
     * If not authenticated → Login. If authenticated, check users/{uid} profile → Onboarding or Home.
     */
    private void navigateByAuthState() {
        AuthRepository authRepository = AuthRepository.getInstance();
        if (authRepository.getCurrentUser() == null) {
            Logger.d(Constants.TAG_AUTH, "Splash: not authenticated, navigating to Login");
            startActivity(clearTaskIntent(LoginActivity.class));
            finish();
            return;
        }

        String uid = authRepository.getCurrentUser().getUid();
        UserRepository.getInstance().getUserProfile(uid, new UserRepository.ProfileLoadCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                if (profile == null || profile.getName() == null || profile.getName().trim().isEmpty()) {
                    Logger.d(Constants.TAG_AUTH, "Splash: no profile, navigating to Onboarding");
                    startActivity(clearTaskIntent(OnboardingActivity.class));
                } else {
                    Logger.d(Constants.TAG_AUTH, "Splash: profile exists, navigating to Home");
                    startActivity(clearTaskIntent(HomeActivity.class));
                }
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Logger.e(Constants.TAG_AUTH, "Splash: profile load failed, sending to Onboarding: " + errorMessage);
                startActivity(clearTaskIntent(OnboardingActivity.class));
                finish();
            }
        });
    }

    private Intent clearTaskIntent(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
