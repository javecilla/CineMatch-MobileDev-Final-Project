package com.example.finalprojectandroiddev2.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;

/**
 * Registration screen for new user sign-up.
 * Firebase Auth logic will be added in a later phase.
 */
public class RegistrationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        applyEdgeToEdgeInsets(R.id.container_registration);

        findViewById(R.id.btn_sign_in).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
