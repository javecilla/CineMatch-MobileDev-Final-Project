package com.example.finalprojectandroiddev2.ui.onboarding;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.repository.AuthRepository;
import com.example.finalprojectandroiddev2.data.repository.UserRepository;
import com.example.finalprojectandroiddev2.model.UserProfile;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.example.finalprojectandroiddev2.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Onboarding screen shown after registration. User must fill name, gender, and birthday
 * before proceeding to Home. Back navigation is disabled.
 */
public class OnboardingActivity extends BaseActivity {

    private TextInputLayout inputName;
    private TextInputLayout inputGender;
    private TextInputLayout inputBirthday;
    private TextInputEditText editName;
    private MaterialAutoCompleteTextView editGender;
    private TextInputEditText editBirthday;
    private TextView textError;
    private MaterialButton btnContinue;

    private AuthRepository authRepository;
    private UserRepository userRepository;
    private boolean isSaving = false;

    /** Birthday stored as yyyy-MM-dd for Firebase */
    private String birthdayValue = null;

    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat STORAGE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        applyEdgeToEdgeInsets(R.id.container_onboarding);

        authRepository = AuthRepository.getInstance();
        userRepository = UserRepository.getInstance();

        inputName = findViewById(R.id.input_name);
        inputGender = findViewById(R.id.input_gender);
        inputBirthday = findViewById(R.id.input_birthday);
        editName = findViewById(R.id.edit_name);
        editGender = findViewById(R.id.edit_gender);
        editBirthday = findViewById(R.id.edit_birthday);
        textError = findViewById(R.id.text_error);
        btnContinue = findViewById(R.id.btn_continue);

        setupGenderDropdown();
        setupBirthdayPicker();
        clearErrorOnType();

        btnContinue.setOnClickListener(v -> submitProfile());

        // Prevent back / swipe back: user must complete the form
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(OnboardingActivity.this, R.string.onboarding_back_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupGenderDropdown() {
        String[] options = new String[]{
                getString(R.string.gender_male),
                getString(R.string.gender_female),
                getString(R.string.gender_other),
                getString(R.string.gender_prefer_not_to_say)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options);
        editGender.setAdapter(adapter);
    }

    private void setupBirthdayPicker() {
        editBirthday.setOnClickListener(v -> {
            clearError();
            showDatePicker();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    cal.set(y, m, d);
                    birthdayValue = STORAGE_FORMAT.format(cal.getTime());
                    editBirthday.setText(DISPLAY_FORMAT.format(cal.getTime()));
                    inputBirthday.setError(null);
                }, year, month, day);
        dialog.show();
    }

    private void clearErrorOnType() {
        editName.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) clearError(); });
        editGender.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) clearError(); });
    }

    private void clearError() {
        textError.setVisibility(View.GONE);
        textError.setText("");
    }

    private void submitProfile() {
        if (isSaving) return;

        clearError();
        inputName.setError(null);
        inputGender.setError(null);
        inputBirthday.setError(null);

        String name = editName.getText() != null ? editName.getText().toString().trim() : "";
        String gender = editGender.getText() != null ? editGender.getText().toString().trim() : "";

        if (!validate(name, gender)) return;

        if (authRepository.getCurrentUser() == null) {
            showError("You are not signed in.");
            return;
        }

        String uid = authRepository.getCurrentUser().getUid();
        String email = authRepository.getCurrentUser().getEmail() != null
                ? authRepository.getCurrentUser().getEmail() : "";

        UserProfile profile = new UserProfile(uid, name, gender, birthdayValue, email);

        setLoadingState(true);
        userRepository.saveUserProfile(profile, new UserRepository.ProfileSaveCallback() {
            @Override
            public void onSuccess() {
                Logger.i(Constants.TAG_AUTH, "Onboarding completed for uid=" + uid);
                setLoadingState(false);
                navigateToHome();
            }

            @Override
            public void onError(String errorMessage) {
                Logger.e(Constants.TAG_AUTH, "Onboarding save failed: " + errorMessage);
                setLoadingState(false);
                showError(errorMessage);
            }
        });
    }

    private boolean validate(String name, String gender) {
        boolean valid = true;
        if (Utils.isBlank(name)) {
            inputName.setError(getString(R.string.onboarding_error_name_required));
            valid = false;
        }
        if (Utils.isBlank(gender)) {
            inputGender.setError(getString(R.string.onboarding_error_gender_required));
            valid = false;
        }
        if (birthdayValue == null || birthdayValue.isEmpty()) {
            inputBirthday.setError(getString(R.string.onboarding_error_birthday_required));
            valid = false;
        }
        return valid;
    }

    private void showError(String message) {
        textError.setText(message);
        textError.setVisibility(View.VISIBLE);
    }

    private void setLoadingState(boolean loading) {
        isSaving = loading;
        btnContinue.setEnabled(!loading);
        btnContinue.setText(loading ? getString(R.string.onboarding_btn_saving) : getString(R.string.onboarding_btn_continue));
        editName.setEnabled(!loading);
        editGender.setEnabled(!loading);
        editBirthday.setEnabled(!loading);
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
