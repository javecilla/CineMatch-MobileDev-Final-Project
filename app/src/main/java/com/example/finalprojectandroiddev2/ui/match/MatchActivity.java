package com.example.finalprojectandroiddev2.ui.match;

import android.content.Intent;
import android.os.Bundle;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.swiping.SwipingActivity;

/**
 * Match Found screen: displays the matched movie with celebration UI.
 * Movie data will come from Firebase matchedMovie node in a later phase.
 */
public class MatchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        applyEdgeToEdgeInsets(R.id.container_match);

        findViewById(R.id.btn_watch_now).setOnClickListener(v -> {
            // Watch Now will open external link or streaming service in later phase
            // For now, just a placeholder
        });

        findViewById(R.id.btn_find_another).setOnClickListener(v -> {
            // Navigate back to swiping session
            startActivity(new Intent(this, SwipingActivity.class));
            finish();
        });

        findViewById(R.id.btn_leave_lobby).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finishAffinity();
        });
    }
}
