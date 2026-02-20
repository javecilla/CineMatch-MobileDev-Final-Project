package com.example.finalprojectandroiddev2.ui.auth;

import android.os.Bundle;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;

/**
 * Login screen with email/password inputs.
 * Navigation and Firebase Auth logic will be added in a later phase.
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        applyEdgeToEdgeInsets(R.id.container_login);
    }
}
