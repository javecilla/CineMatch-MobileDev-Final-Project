package com.example.finalprojectandroiddev2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.lobby.CreateLobbyActivity;
import com.example.finalprojectandroiddev2.ui.lobby.JoinLobbyActivity;

/**
 * Home screen with top nav bar (navbar brand + menu button), welcome text, Create Lobby and Join Lobby.
 * Menu button will open sidebar (collapsible) when implemented.
 */
public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        applyEdgeToEdgeInsets(R.id.container_home);

        // Menu button: placeholder until sidebar is implemented
        findViewById(R.id.btn_menu).setOnClickListener(v ->
                Toast.makeText(this, R.string.msg_menu_coming_soon, Toast.LENGTH_SHORT).show());

        findViewById(R.id.btn_create_lobby).setOnClickListener(v ->
                startActivity(new Intent(this, CreateLobbyActivity.class)));

        findViewById(R.id.btn_join_lobby).setOnClickListener(v ->
                startActivity(new Intent(this, JoinLobbyActivity.class)));
    }
}
