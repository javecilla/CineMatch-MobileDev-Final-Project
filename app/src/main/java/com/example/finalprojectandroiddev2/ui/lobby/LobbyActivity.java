package com.example.finalprojectandroiddev2.ui.lobby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;

/**
 * Lobby screen: room code, member list with status, Start Swiping (host), Leave Lobby.
 * Firebase sync and swiping navigation will be added in a later phase.
 */
public class LobbyActivity extends BaseActivity {

    public static final String EXTRA_ROOM_CODE = "room_code";
    public static final String EXTRA_IS_HOST = "is_host";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        applyEdgeToEdgeInsets(R.id.container_lobby);

        String roomCode = getIntent().getStringExtra(EXTRA_ROOM_CODE);
        boolean isHost = getIntent().getBooleanExtra(EXTRA_IS_HOST, false);

        ((TextView) findViewById(R.id.text_room_code)).setText(roomCode != null ? roomCode : "------");

        findViewById(R.id.btn_start_swiping).setVisibility(isHost ? View.VISIBLE : View.GONE);

        RecyclerView recyclerMembers = findViewById(R.id.recycler_members);
        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        MemberAdapter adapter = new MemberAdapter();
        recyclerMembers.setAdapter(adapter);
        // Placeholder: show host as online when host
        if (isHost) {
            adapter.setMembers(java.util.Collections.singletonList(
                    new MemberAdapter.MemberItem("You", true, getString(R.string.status_online))));
        }

        findViewById(R.id.btn_leave_lobby).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}
