package com.example.finalprojectandroiddev2.ui.lobby;

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

        // Placeholder room code until Firebase generates real one
        ((TextView) findViewById(R.id.text_room_code)).setText("------");

        RecyclerView recyclerMembers = findViewById(R.id.recycler_members);
        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerMembers.setAdapter(new MemberAdapter());
    }
}
