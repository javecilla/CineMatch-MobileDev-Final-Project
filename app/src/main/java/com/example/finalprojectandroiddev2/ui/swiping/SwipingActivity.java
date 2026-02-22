package com.example.finalprojectandroiddev2.ui.swiping;

import android.content.Intent;
import android.os.Bundle;

import androidx.viewpager2.widget.ViewPager2;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;

/**
 * Swiping screen: displays movie cards for swiping Yes/No.
 * ViewPager2 adapter, swipe gestures, and Firebase vote sync will be added in a later phase.
 */
public class SwipingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiping);
        applyEdgeToEdgeInsets(R.id.container_swiping);

        ViewPager2 viewPagerMovies = findViewById(R.id.viewpager_movies);
        // ViewPager2 adapter will be set when TMDB movies are fetched
        // For now, ViewPager2 is configured but empty

        // Navigate to HomeActivity (not LobbyActivity) — the lobby session is already
        // complete at this point and LobbyActivity requires a room_code extra to function.
        findViewById(R.id.btn_exit_session).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });

        // Yes/No button click handlers will be added when swipe logic is implemented
    }
}
