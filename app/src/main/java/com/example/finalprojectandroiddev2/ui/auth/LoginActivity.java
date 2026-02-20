package com.example.finalprojectandroiddev2.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;

/**
 * Login screen with email/password inputs.
 * Firebase Auth logic will be added in a later phase.
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        applyEdgeToEdgeInsets(R.id.container_login);

        findViewById(R.id.btn_register).setOnClickListener(v ->
                startActivity(new Intent(this, RegistrationActivity.class)));

        findViewById(R.id.btn_sign_in).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}
