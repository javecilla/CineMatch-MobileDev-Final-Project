package com.example.finalprojectandroiddev2.ui.lobby;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;

/**
 * Create lobby screen: room code display, share, member list, start swiping.
 * Firebase and lobby logic will be added in a later phase.
 */
public class CreateLobbyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        applyEdgeToEdgeInsets(R.id.container_create_lobby);

        String placeholderCode = "ABC123";
        ((TextView) findViewById(R.id.text_room_code)).setText(placeholderCode);

        RecyclerView recyclerMembers = findViewById(R.id.recycler_members);
        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerMembers.setAdapter(new MemberAdapter());

        findViewById(R.id.btn_start_swiping).setEnabled(true);
        findViewById(R.id.btn_start_swiping).setOnClickListener(v -> {
            Intent i = new Intent(this, LobbyActivity.class);
            i.putExtra(LobbyActivity.EXTRA_ROOM_CODE, placeholderCode);
            i.putExtra(LobbyActivity.EXTRA_IS_HOST, true);
            startActivity(i);
            finish();
        });
    }
}
