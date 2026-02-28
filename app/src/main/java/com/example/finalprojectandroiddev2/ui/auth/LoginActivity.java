package com.example.finalprojectandroiddev2.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ScrollView;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.repository.AuthRepository;
import com.example.finalprojectandroiddev2.data.repository.UserRepository;
import com.example.finalprojectandroiddev2.model.User;
import com.example.finalprojectandroiddev2.model.UserProfile;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.onboarding.OnboardingActivity;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.example.finalprojectandroiddev2.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

/**
 * Login screen with email/password inputs.
 * Handles Firebase Authentication sign-in flow.
 */
public class LoginActivity extends BaseActivity {

    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private TextView textError;
    private MaterialButton btnSignIn;
    private TextView btnRegister;

    private AuthRepository authRepository;
    private boolean isSigningIn = false;
    private long lastBackPressTime = 0;
    private static final long BACK_PRESS_INTERVAL_MS = 2000;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // We override edge-to-edge logic to specifically include the software keyboard (IME)
        View container = findViewById(R.id.container_login);
        ScrollView scrollLogin = findViewById(R.id.scroll_login);
        ViewCompat.setOnApplyWindowInsetsListener(container, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomInset = Math.max(systemBars.bottom, ime.bottom);

            // Pad the ScrollView directly so the background image remains fullscreen
            // but the form content is pushed upward nicely when keyboard shows.
            scrollLogin.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomInset);
            return WindowInsetsCompat.CONSUMED;
        });

        authRepository = AuthRepository.getInstance();

        // Initialize views
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        textError = findViewById(R.id.text_error);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnRegister = findViewById(R.id.btn_register);

        // Clear error when user types
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sign in button click
        btnSignIn.setOnClickListener(v -> attemptSignIn());

        // Register button click
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrationActivity.class)));

        // Prevent back navigation to Splash; show exit confirmation on first back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                long now = System.currentTimeMillis();
                if (now - lastBackPressTime < BACK_PRESS_INTERVAL_MS) {
                    finishAffinity();
                } else {
                    lastBackPressTime = now;
                    Toast.makeText(LoginActivity.this, R.string.msg_press_back_again_to_exit, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Attempts to sign in with email and password.
     */
    private void attemptSignIn() {
        if (isSigningIn) {
            return; // Prevent multiple simultaneous sign-in attempts
        }

        // Clear previous errors
        clearError();
        inputEmail.setError(null);
        inputPassword.setError(null);

        // Get input values
        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String password = editPassword.getText() != null ? editPassword.getText().toString() : "";

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Call AuthRepository
        authRepository.signIn(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                Logger.i(Constants.TAG_AUTH, "Login successful for user: " + user.getEmail());
                setLoadingState(false);
                navigateByProfile(user.getUid());
            }

            @Override
            public void onError(String errorMessage) {
                Logger.e(Constants.TAG_AUTH, "Login failed: " + errorMessage);
                setLoadingState(false);
                showError(errorMessage);
            }
        });
    }

    /**
     * If user has no profile (users/{uid}) → Onboarding; otherwise → Home.
     */
    private void navigateByProfile(String uid) {
        UserRepository.getInstance().getUserProfile(uid, new UserRepository.ProfileLoadCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                if (profile == null || profile.getName() == null || profile.getName().trim().isEmpty()) {
                    startActivity(clearTaskIntent(OnboardingActivity.class));
                } else {
                    startActivity(clearTaskIntent(HomeActivity.class));
                }
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                Logger.e(Constants.TAG_AUTH, "Login: profile load failed, sending to Onboarding: " + errorMessage);
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

    /**
     * Validates email and password inputs.
     *
     * @param email    Email to validate
     * @param password Password to validate
     * @return true if valid, false otherwise
     */
    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        // Validate email
        if (Utils.isBlank(email)) {
            inputEmail.setError(getString(R.string.error_email_required));
            isValid = false;
        } else if (!isValidEmail(email)) {
            inputEmail.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        // Validate password
        if (Utils.isBlank(password)) {
            inputPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (password.length() < 6) {
            inputPassword.setError(getString(R.string.error_password_too_short));
            isValid = false;
        }

        return isValid;
    }

    /**
     * Validates email format using regex pattern.
     */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Shows error message in the error TextView.
     */
    private void showError(String message) {
        textError.setText(message);
        textError.setVisibility(View.VISIBLE);
    }

    /**
     * Clears error message.
     */
    private void clearError() {
        textError.setVisibility(View.GONE);
        textError.setText("");
    }

    /**
     * Sets loading state (disables button, shows/hides loading indicator).
     */
    private void setLoadingState(boolean loading) {
        isSigningIn = loading;
        btnSignIn.setEnabled(!loading);
        btnSignIn.setText(loading ? getString(R.string.btn_signing_in) : getString(R.string.btn_sign_in));
        editEmail.setEnabled(!loading);
        editPassword.setEnabled(!loading);
        btnRegister.setEnabled(!loading);
    }

}
