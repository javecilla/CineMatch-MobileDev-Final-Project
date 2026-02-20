package com.example.finalprojectandroiddev2.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.repository.AuthRepository;
import com.example.finalprojectandroiddev2.model.User;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.example.finalprojectandroiddev2.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

/**
 * Registration screen for new user sign-up.
 * Handles Firebase Authentication sign-up flow.
 */
public class RegistrationActivity extends BaseActivity {

    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private TextInputLayout inputConfirmPassword;
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private TextInputEditText editConfirmPassword;
    private TextView textError;
    private MaterialButton btnRegister;
    private TextView btnSignIn;

    private AuthRepository authRepository;
    private boolean isSigningUp = false;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        applyEdgeToEdgeInsets(R.id.container_registration);

        authRepository = AuthRepository.getInstance();

        // Initialize views
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        textError = findViewById(R.id.text_error);
        btnRegister = findViewById(R.id.btn_register);
        btnSignIn = findViewById(R.id.btn_sign_in);

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
                // Clear confirm password error when password changes
                inputConfirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearError();
                inputConfirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Register button click
        btnRegister.setOnClickListener(v -> attemptSignUp());

        // Sign in button click
        btnSignIn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Attempts to sign up with email and password.
     */
    private void attemptSignUp() {
        if (isSigningUp) {
            return; // Prevent multiple simultaneous sign-up attempts
        }

        // Clear previous errors
        clearError();
        inputEmail.setError(null);
        inputPassword.setError(null);
        inputConfirmPassword.setError(null);

        // Get input values
        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String password = editPassword.getText() != null ? editPassword.getText().toString() : "";
        String confirmPassword = editConfirmPassword.getText() != null ? editConfirmPassword.getText().toString() : "";

        // Validate inputs
        if (!validateInputs(email, password, confirmPassword)) {
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Call AuthRepository
        authRepository.signUp(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                Logger.i(Constants.TAG_AUTH, "Registration successful for user: " + user.getEmail());
                setLoadingState(false);
                navigateToHome();
            }

            @Override
            public void onError(String errorMessage) {
                Logger.e(Constants.TAG_AUTH, "Registration failed: " + errorMessage);
                setLoadingState(false);
                showError(errorMessage);
            }
        });
    }

    /**
     * Validates email, password, and confirm password inputs.
     *
     * @param email          Email to validate
     * @param password       Password to validate
     * @param confirmPassword Confirm password to validate
     * @return true if valid, false otherwise
     */
    private boolean validateInputs(String email, String password, String confirmPassword) {
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

        // Validate confirm password
        if (Utils.isBlank(confirmPassword)) {
            inputConfirmPassword.setError(getString(R.string.error_confirm_password_required));
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            inputConfirmPassword.setError(getString(R.string.error_passwords_not_match));
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
        isSigningUp = loading;
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? getString(R.string.btn_registering) : getString(R.string.btn_register));
        editEmail.setEnabled(!loading);
        editPassword.setEnabled(!loading);
        editConfirmPassword.setEnabled(!loading);
        btnSignIn.setEnabled(!loading);
    }

    /**
     * Navigates to HomeActivity and finishes this activity.
     */
    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
