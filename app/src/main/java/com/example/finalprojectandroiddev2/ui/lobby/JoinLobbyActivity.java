package com.example.finalprojectandroiddev2.ui.lobby;

import android.os.Bundle;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;

/**
 * Join lobby screen: enter room code to join an existing lobby.
 * Firebase validation and navigation to LobbyActivity will be added in a later phase.
 */
public class JoinLobbyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);
        applyEdgeToEdgeInsets(R.id.container_join_lobby);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}
