package com.example.finalprojectandroiddev2.ui.match;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
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
    private MaterialButton       btnWatchNow;
    private MaterialButton       btnFindAnother;
    private LottieAnimationView  lottieConfetti;

    // ── State ────────────────────────────────────────────────────────────────

    private FirebaseRepository firebaseRepo;
    private String             roomCode;
    private boolean            isHost;
    private String             tmdbUrl; // built once movie details arrive

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

        // Start confetti immediately — it plays while movie data loads
        lottieConfetti.playAnimation();

        // 1. Fetch matched movie ID from Firebase, then load TMDB details
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
        imgPoster      = findViewById(R.id.image_movie_poster);
        tvTitle        = findViewById(R.id.text_movie_title);
        tvRating       = findViewById(R.id.text_movie_rating);
        tvReleaseDate  = findViewById(R.id.text_movie_release_date);
        tvOverview     = findViewById(R.id.text_movie_overview);
        btnWatchNow    = findViewById(R.id.btn_watch_now);
        btnFindAnother = findViewById(R.id.btn_find_another);
        lottieConfetti = findViewById(R.id.lottie_confetti);
    }

    private void setupButtons() {
        // Watch on TMDb button — opens TMDB movie page in browser
        // URL is populated once movie details arrive; button is disabled until then
        btnWatchNow.setEnabled(false);
        btnWatchNow.setOnClickListener(v -> {
            if (tmdbUrl != null) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(tmdbUrl)));
            }
        });

        // Find Another Match — reset session and restart swiping
        findViewById(R.id.btn_find_another).setOnClickListener(v -> restartSwipingSession());

        // Leave Lobby — go home and clear activity stack
        findViewById(R.id.btn_leave_lobby).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finishAffinity();
        });
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
        // Poster
        String posterUrl = movie.getPosterPath() != null
                ? Constants.TMDB_IMAGE_BASE_URL + movie.getPosterPath()
                : null;
        if (posterUrl != null) {
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(imgPoster);
        }

        // Text fields
        tvTitle.setText(movie.getTitle() != null ? movie.getTitle() : "—");
        tvRating.setText(String.format("⭐ %.1f", movie.getVoteAverage()));
        tvReleaseDate.setText(formatYear(movie.getReleaseDate()));
        tvOverview.setText(movie.getOverview() != null ? movie.getOverview() : "");

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

    /** Extracts the 4-digit year from a "YYYY-MM-DD" string. */
    private String formatYear(String releaseDate) {
        if (releaseDate == null || releaseDate.length() < 4) return "—";
        return releaseDate.substring(0, 4);
    }
}
