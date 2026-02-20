package com.example.finalprojectandroiddev2.ui.lobby;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;

/**
 * Lobby screen: displays room code, member list, and controls for starting swiping or leaving.
 * Firebase real-time updates and member status logic will be added in a later phase.
 */
public class LobbyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        applyEdgeToEdgeInsets(R.id.container_lobby);

        // Placeholder room code until Firebase provides real one
        // Room code will come from Intent extras or Firebase in later phase

        RecyclerView recyclerMembers = findViewById(R.id.recycler_members);
        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerMembers.setAdapter(new MemberAdapter());

        findViewById(R.id.btn_leave_lobby).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        // Start Swiping button visibility and click handler will be set based on host status
        // Visibility controlled by Firebase listener in later phase
    }
}
