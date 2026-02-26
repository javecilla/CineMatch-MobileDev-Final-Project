package com.example.finalprojectandroiddev2.ui.watch;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.OnBackPressedCallback;

import com.bumptech.glide.Glide;

import com.example.finalprojectandroiddev2.BuildConfig;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.api.TmdbApiClient;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.data.repository.FirebaseRepository;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.lobby.LobbyActivity;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.LobbyPrefs;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Watch screen shown after the host taps "Watch Now" on MatchActivity.
 * Plays a default intro video and shows the matched movie details.
 */
public class WatchActivity extends BaseActivity {

    private static final String TAG = "CineMatch.Watch";

    private String roomCode;
    private boolean isHost;

    private FirebaseRepository firebaseRepo;

    // Views
    private TextView    tvWatchInfo;
    private VideoView   videoIntro;
    private android.widget.LinearLayout layoutHostActions;
    private MaterialButton btnPlay;
    private MaterialButton btnDone;
    private TextView    tvMemberStatus;
    private ImageView   imgPoster;

    // Movie info views (mirrors MatchActivity)
    private TextView   tvTitle;
    private TextView   tvRating;
    private TextView   tvReleaseDate;
    private TextView   tvOverview;
    private ChipGroup  chipGroupGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        applyEdgeToEdgeInsets(R.id.container_watch);

        roomCode  = getIntent().getStringExtra(LobbyActivity.EXTRA_ROOM_CODE);
        isHost    = getIntent().getBooleanExtra(LobbyActivity.EXTRA_IS_HOST, false);
        firebaseRepo = FirebaseRepository.getInstance();

        // Disable back navigation to keep flow controlled (user should use Done Watching).
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // no-op
            }
        });

        bindViews();
        setupVideo();
        setupButtons();
        loadWatchingInfo();
        loadMatchedMovieDetails();
        listenForStatusChanges();
    }

    private void bindViews() {
        tvWatchInfo       = findViewById(R.id.text_watch_info);
        videoIntro        = findViewById(R.id.video_intro);
        layoutHostActions = findViewById(R.id.layout_host_actions);
        btnPlay           = findViewById(R.id.btn_play);
        btnDone           = findViewById(R.id.btn_done_watching);
        tvMemberStatus    = findViewById(R.id.text_member_status);
        imgPoster         = findViewById(R.id.image_movie_poster);

        tvTitle       = findViewById(R.id.text_movie_title);
        tvRating      = findViewById(R.id.text_movie_rating);
        tvReleaseDate = findViewById(R.id.text_movie_release_date);
        tvOverview    = findViewById(R.id.text_movie_overview);
        chipGroupGenres = findViewById(R.id.chip_group_genres);
    }

    private void setupVideo() {
        // The actual .mp4 file must live under res/raw as cinematch_default_intro_video.mp4.
        // Make sure to place it there in the project for this URI to resolve.
        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cinematch_default_intro_video);
            videoIntro.setVideoURI(uri);
            // Force the video to render its first frame so it doesn't just show a black box
            videoIntro.setOnPreparedListener(mp -> mp.seekTo(1));
        } catch (Exception e) {
            Logger.e(TAG, "Failed to set video URI: " + e.getMessage());
        }
    }

    private void setupButtons() {
        if (isHost) {
            layoutHostActions.setVisibility(View.VISIBLE);
            tvMemberStatus.setVisibility(View.GONE);

            btnPlay.setOnClickListener(v -> {
                if (videoIntro != null && roomCode != null) {
                    firebaseRepo.setLobbyStatus(roomCode, Constants.LOBBY_STATUS_PLAYING);
                    videoIntro.start();
                }
            });

            btnDone.setOnClickListener(v -> {
                if (roomCode != null) {
                    firebaseRepo.setLobbyStatus(roomCode, Constants.LOBBY_STATUS_COMPLETED);
                }
                navigateToHome();
            });
        } else {
            layoutHostActions.setVisibility(View.GONE);
            tvMemberStatus.setVisibility(View.VISIBLE);
            tvMemberStatus.setText(getString(R.string.msg_wait_host_play));
        }
    }

    private void listenForStatusChanges() {
        if (roomCode == null || roomCode.isEmpty()) return;
        
        firebaseRepo.listenLobbyStatus(roomCode, status -> {
            if (Constants.LOBBY_STATUS_PLAYING.equals(status)) {
                runOnUiThread(() -> {
                    if (!isHost) {
                        tvMemberStatus.setText(getString(R.string.msg_movie_is_playing));
                        if (videoIntro != null) {
                            videoIntro.start();
                        }
                    }
                });
            } else if (Constants.LOBBY_STATUS_COMPLETED.equals(status)) {
                if (!isHost) {
                    runOnUiThread(this::navigateToHome);
                }
            }
        });
    }

    private void navigateToHome() {
        if (isFinishing() || isDestroyed()) return;
        firebaseRepo.detachLobbyListeners();
        LobbyPrefs.clearActiveRoomCode(this);

        android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }

    /**
     * Loads member count and updates the watch info message.
     */
    private void loadWatchingInfo() {
        if (roomCode == null || roomCode.isEmpty()) {
            tvWatchInfo.setText(getString(R.string.watch_info_alone));
            return;
        }

        firebaseRepo.loadAllMembers(roomCode, members -> {
            int total = (members != null) ? members.size() : 1;
            runOnUiThread(() -> {
                if (total <= 1) {
                    tvWatchInfo.setText(getString(R.string.watch_info_alone));
                } else {
                    int others = total - 1;
                    tvWatchInfo.setText(
                            getString(R.string.watch_info_others, others)
                    );
                }
            });
        });
    }

    /**
     * Reads matchedMovieId from Firebase and fetches movie details from TMDB.
     */
    private void loadMatchedMovieDetails() {
        if (roomCode == null || roomCode.isEmpty()) return;

        firebaseRepo.getMatchedMovieId(roomCode, movieIdStr -> {
            if (movieIdStr == null) {
                Logger.d(TAG, "matchedMovieId not found for watch screen");
                return;
            }
            try {
                int movieId = Integer.parseInt(movieIdStr);
                fetchMovieDetails(movieId);
            } catch (NumberFormatException e) {
                Logger.d(TAG, "Invalid matchedMovieId for watch: " + movieIdStr);
            }
        });
    }

    private void fetchMovieDetails(int movieId) {
        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiClient.getService()
                .getMovieDetails(movieId, "en-US", bearer)
                .enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Logger.d(TAG, "Watch getMovieDetails failed: " + response.code());
                            return;
                        }
                        runOnUiThread(() -> bindMovie(response.body()));
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        Logger.d(TAG, "Watch getMovieDetails network error: " + t.getMessage());
                    }
                });
    }

    private void bindMovie(Movie movie) {
        if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
            String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
            Glide.with(this).load(posterUrl).into(imgPoster);
        }

        tvTitle.setText(movie.getTitle() != null ? movie.getTitle() : "—");
        tvRating.setText(String.format("%.1f", movie.getVoteAverage()));
        tvReleaseDate.setText(formatReleaseDate(movie.getReleaseDate()));
        tvOverview.setText(movie.getOverview() != null ? movie.getOverview() : "");

        chipGroupGenres.removeAllViews();
        if (movie.getGenres() != null) {
            for (Movie.Genre genre : movie.getGenres()) {
                Chip chip = new Chip(this);
                chip.setText(genre.getName());
                chip.setClickable(false);
                chip.setCheckable(false);
                chip.setChipBackgroundColorResource(android.R.color.transparent);
                chip.setTextColor(getColor(R.color.white));
                chip.setChipStrokeColorResource(R.color.color_text_secondary);
                chip.setChipStrokeWidth(1.5f);
                chip.setTextSize(11f);
                chipGroupGenres.addView(chip);
            }
        }
    }

    private String formatReleaseDate(String releaseDate) {
        if (releaseDate == null || releaseDate.length() < 7) return "—";
        try {
            java.text.SimpleDateFormat inputFmt  =
                    new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            java.text.SimpleDateFormat outputFmt =
                    new java.text.SimpleDateFormat("MMM yyyy",   java.util.Locale.US);
            java.util.Date date = inputFmt.parse(releaseDate);
            return date != null ? outputFmt.format(date) : releaseDate.substring(0, 7);
        } catch (java.text.ParseException e) {
            return releaseDate.substring(0, 4);
        }
    }
}

