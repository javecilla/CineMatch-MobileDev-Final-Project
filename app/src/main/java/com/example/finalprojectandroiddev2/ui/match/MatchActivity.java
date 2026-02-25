package com.example.finalprojectandroiddev2.ui.match;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.finalprojectandroiddev2.BuildConfig;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.api.TmdbApiClient;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.data.repository.FirebaseRepository;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.lobby.LobbyActivity;
import com.example.finalprojectandroiddev2.ui.swiping.SwipingActivity;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Displayed when the lobby reaches a unanimous Yes match.
 *
 * Flow:
 *  1. Read matchedMovieId from Firebase (set by FirebaseRepository.checkForMatch)
 *  2. Fetch full movie details from TMDB /movie/{id}
 *  3. Bind poster, title, rating, release year, overview
 *  4. Play Lottie confetti celebration animation
 *  5. Offer: Watch on TMDb | Find Another Match | Leave Lobby
 */
public class MatchActivity extends BaseActivity {

    private static final String TAG = "CineMatch.Match";

    // ── Extras ───────────────────────────────────────────────────────────────

    /** Pass the room code so we can fetch matchedMovieId from Firebase. */
    public static final String EXTRA_ROOM_CODE = LobbyActivity.EXTRA_ROOM_CODE;

    /** Pass isHost so "Find Another Match" can restart swiping correctly. */
    public static final String EXTRA_IS_HOST   = LobbyActivity.EXTRA_IS_HOST;

    // ── Views ────────────────────────────────────────────────────────────────

    private ImageView            imgPoster;
    private TextView             tvTitle;
    private TextView             tvRating;
    private TextView             tvReleaseDate;
    private TextView             tvOverview;
    private TextView             tvMemberCount;
    private ChipGroup            chipGroupGenres;
    private MaterialButton       btnWatchNow;
    private MaterialButton       btnFindAnother;
    private LottieAnimationView  lottieConfetti;
    // Role-based action groups
    private LinearLayout         layoutHostActions;
    private LinearLayout         layoutMemberActions;

    // ── State ────────────────────────────────────────────────────────────────

    private FirebaseRepository firebaseRepo;
    private String             roomCode;
    private boolean            isHost;
    private String             tmdbUrl;      // built once movie details arrive
    private int                liveCount = 0; // current members in lobby
    private int                maxCount  = 0; // snapshot size at load time

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        applyEdgeToEdgeInsets(R.id.container_match);

        roomCode     = getIntent().getStringExtra(EXTRA_ROOM_CODE);
        isHost       = getIntent().getBooleanExtra(EXTRA_IS_HOST, false);
        firebaseRepo = FirebaseRepository.getInstance();

        bindViews();
        setupButtons();
        startMemberCountListener();

        // Start confetti immediately
        lottieConfetti.playAnimation();

        // Fetch matched movie ID then load TMDB details
        if (roomCode != null && !roomCode.isEmpty()) {
            firebaseRepo.getMatchedMovieId(roomCode, movieIdStr -> {
                if (movieIdStr == null) {
                    Logger.d(TAG, "matchedMovieId not found in Firebase");
                    return;
                }
                try {
                    int movieId = Integer.parseInt(movieIdStr);
                    fetchMovieDetails(movieId);
                } catch (NumberFormatException e) {
                    Logger.d(TAG, "Invalid matchedMovieId: " + movieIdStr);
                }
            });
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void bindViews() {
        imgPoster           = findViewById(R.id.image_movie_poster);
        tvTitle             = findViewById(R.id.text_movie_title);
        tvRating            = findViewById(R.id.text_movie_rating);
        tvReleaseDate       = findViewById(R.id.text_movie_release_date);
        tvOverview          = findViewById(R.id.text_movie_overview);
        tvMemberCount       = findViewById(R.id.text_member_count);
        chipGroupGenres     = findViewById(R.id.chip_group_genres);
        btnWatchNow         = findViewById(R.id.btn_watch_now);
        btnFindAnother      = findViewById(R.id.btn_find_another);
        lottieConfetti      = findViewById(R.id.lottie_confetti);
        layoutHostActions   = findViewById(R.id.layout_host_actions);
        layoutMemberActions = findViewById(R.id.layout_member_actions);
    }

    /**
     * Loads the initial member count then attaches a live listener.
     * Displays as "current/max" in text_member_count.
     */
    private void startMemberCountListener() {
        if (roomCode == null || roomCode.isEmpty()) return;

        // One-shot read to establish the initial count (= max for this session)
        firebaseRepo.loadAllMembers(roomCode, members -> {
            maxCount = members != null ? members.size() : 0;
            liveCount = maxCount;
            updateMemberCountUi();
        });

        // Live updates: track members joining / leaving
        firebaseRepo.listenMembers(roomCode, new FirebaseRepository.MembersCallback() {
            @Override public void onMemberAdded(String userId, com.example.finalprojectandroiddev2.data.model.LobbyMember member) {
                // Only increment if already past the initial load
                if (maxCount > 0) { liveCount = Math.min(liveCount + 1, maxCount); updateMemberCountUi(); }
            }
            @Override public void onMemberRemoved(String userId) {
                liveCount = Math.max(0, liveCount - 1);
                updateMemberCountUi();
            }
            @Override public void onMemberChanged(String userId, com.example.finalprojectandroiddev2.data.model.LobbyMember member) { /* no-op */ }
        });
    }

    private void updateMemberCountUi() {
        runOnUiThread(() -> {
            if (tvMemberCount != null) {
                tvMemberCount.setText(liveCount + "/" + maxCount);
            }
        });
    }

    private void setupButtons() {
        if (isHost) {
            // Host: Watch Now (full-width) + Find Another Match link
            layoutHostActions.setVisibility(View.VISIBLE);
            layoutMemberActions.setVisibility(View.GONE);

            // Watch Now — disabled until TMDB details load
            btnWatchNow.setEnabled(false);
            btnWatchNow.setOnClickListener(v -> {
                if (tmdbUrl != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(tmdbUrl)));
                }
            });

            // Find Another Match
            btnFindAnother.setOnClickListener(v -> restartSwipingSession());

        } else {
            // Member: wait info text only — no buttons
            layoutHostActions.setVisibility(View.GONE);
            layoutMemberActions.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Disables the Android back button. Users should not be able to accidentally
     * leave the Match screen — they committed to this movie by voting!
     */
    @Override
    public void onBackPressed() {
        // Intentionally disabled — no accidental back navigation from Match screen
    }

    /**
     * Fetches full movie details from TMDB and binds them to the UI.
     */
    private void fetchMovieDetails(int movieId) {
        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiClient.getService()
                .getMovieDetails(movieId, "en-US", bearer)
                .enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Logger.d(TAG, "getMovieDetails failed: " + response.code());
                            return;
                        }
                        runOnUiThread(() -> bindMovie(response.body()));
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        Logger.d(TAG, "getMovieDetails network error: " + t.getMessage());
                    }
                });
    }

    /**
     * Populates all UI views with movie data.
     */
    private void bindMovie(Movie movie) {
        // Poster — use backdrop for full-screen hero; fall back to poster path
        String imageUrl = movie.getBackdropPath() != null
                ? Constants.TMDB_IMAGE_BASE_URL + movie.getBackdropPath()
                : (movie.getPosterPath() != null
                        ? Constants.TMDB_IMAGE_BASE_URL + movie.getPosterPath()
                        : null);
        if (imageUrl != null) {
            int radiusPx = Math.round(16 * getResources().getDisplayMetrics().density);
            Glide.with(this)
                    .load(imageUrl)
                    .transform(new RoundedCorners(radiusPx))
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(imgPoster);
        }

        // Title
        tvTitle.setText(movie.getTitle() != null ? movie.getTitle() : "—");

        // Rating — plain number, icon is in the layout
        tvRating.setText(String.format("%.1f", movie.getVoteAverage()));

        // Release date formatted as "Feb 2025"
        tvReleaseDate.setText(formatReleaseDate(movie.getReleaseDate()));

        // Overview
        tvOverview.setText(movie.getOverview() != null ? movie.getOverview() : "");

        // Genre chips — style matches MovieCardAdapter
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

        // Build TMDb URL and enable watch button
        tmdbUrl = "https://www.themoviedb.org/movie/" + movie.getId();
        btnWatchNow.setEnabled(true);
    }

    /**
     * Resets Firebase state and navigates back to SwipingActivity for a new round.
     */
    private void restartSwipingSession() {
        if (roomCode == null) return;
        // Reset page + status so all devices re-enter swiping
        firebaseRepo.setCurrentPage(roomCode, 0);
        firebaseRepo.setLobbyStatus(roomCode, Constants.LOBBY_STATUS_SWIPING);

        Intent intent = new Intent(this, SwipingActivity.class);
        intent.putExtra(LobbyActivity.EXTRA_ROOM_CODE, roomCode);
        intent.putExtra(LobbyActivity.EXTRA_IS_HOST, isHost);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Formats a "YYYY-MM-DD" release date string into "MMM yyyy" (e.g. "Feb 2025").
     * Falls back to "—" for null / too-short strings.
     */
    private String formatReleaseDate(String releaseDate) {
        if (releaseDate == null || releaseDate.length() < 7) return "—";
        try {
            java.text.SimpleDateFormat inputFmt  = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
            java.text.SimpleDateFormat outputFmt = new java.text.SimpleDateFormat("MMM yyyy",   java.util.Locale.US);
            java.util.Date date = inputFmt.parse(releaseDate);
            return date != null ? outputFmt.format(date) : releaseDate.substring(0, 7);
        } catch (java.text.ParseException e) {
            return releaseDate.substring(0, 4); // best-effort year fallback
        }
    }
}
