package com.example.finalprojectandroiddev2.ui.home;

import android.content.Intent;
import android.os.Bundle;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.auth.LoginActivity;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.lobby.CreateLobbyActivity;
import com.example.finalprojectandroiddev2.ui.lobby.JoinLobbyActivity;

/**
 * Home screen (main page) with Create Lobby and Join Lobby options.
 * Lobby navigation and logout logic will be added in a later phase.
 */
public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        applyEdgeToEdgeInsets(R.id.container_home);

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });

        findViewById(R.id.btn_create_lobby).setOnClickListener(v ->
                startActivity(new Intent(this, CreateLobbyActivity.class)));

        findViewById(R.id.btn_join_lobby).setOnClickListener(v ->
                startActivity(new Intent(this, JoinLobbyActivity.class)));
    }
}
