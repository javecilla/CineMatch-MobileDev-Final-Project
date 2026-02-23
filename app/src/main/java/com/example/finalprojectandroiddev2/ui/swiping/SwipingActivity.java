package com.example.finalprojectandroiddev2.ui.swiping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalprojectandroiddev2.BuildConfig;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.api.TmdbApiClient;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.data.model.MovieListResponse;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.lobby.LobbyActivity;
import com.example.finalprojectandroiddev2.utils.LobbyPrefs;
import com.example.finalprojectandroiddev2.utils.Logger;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Swiping screen: displays a full-screen ViewPager2 deck of movie cards.
 *
 * Every device fetches the same TMDB endpoint (trending/day, page 1) directly.
 * Because page 1 is deterministic and identical for all clients at a given moment,
 * all users in the same lobby see the same movies in the same order — no Firebase
 * coordination required for the movie list itself.
 */
public class SwipingActivity extends BaseActivity {

    private static final String TAG = "CineMatch.Swiping";

    private ViewPager2       viewPagerMovies;
    private MovieCardAdapter movieCardAdapter;
    private String           roomCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiping);
        applyEdgeToEdgeInsets(R.id.container_swiping);

        // Session has started — clear the active lobby so the "Tap to return to lobby"
        // banner never appears on the Home screen while/after swiping.
        LobbyPrefs.clearActiveRoomCode(this);

        roomCode = getIntent().getStringExtra(LobbyActivity.EXTRA_ROOM_CODE);

        bindViews();
        setupViewPager();
        setupButtons();
        fetchMovies();
    }

    // ── Views ──────────────────────────────────────────────────────────────────

    private void bindViews() {
        viewPagerMovies = findViewById(R.id.viewpager_movies);
    }

    // ── ViewPager2 setup ───────────────────────────────────────────────────────

    private void setupViewPager() {
        movieCardAdapter = new MovieCardAdapter();
        viewPagerMovies.setAdapter(movieCardAdapter);

        // Disable free swiping — cards only advance via the Yes / No buttons.
        // This ensures every card transition is a deliberate vote, not an accidental swipe.
        // (Phase 7.2 will add swipe-left = No / swipe-right = Yes touch shortcuts on the card itself.)
        viewPagerMovies.setUserInputEnabled(false);

        viewPagerMovies.setOffscreenPageLimit(2);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(24));
        transformer.addTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setScaleY(1f - absPos * 0.08f);
            page.setAlpha(1f  - absPos * 0.3f);
        });
        viewPagerMovies.setPageTransformer(transformer);
    }

    // ── Buttons ────────────────────────────────────────────────────────────────

    private void setupButtons() {
        // Yes / No — vote logic will be wired in Phase 7.2
        findViewById(R.id.btn_swipe_yes).setOnClickListener(v -> handleYes());
        findViewById(R.id.btn_swipe_no).setOnClickListener(v  -> handleNo());

        // Exit session
        findViewById(R.id.btn_exit_session).setOnClickListener(v -> {
            LobbyPrefs.clearActiveRoomCode(this);
            startActivity(new Intent(this, HomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }

    // ── Yes / No stubs (Phase 7.2) ─────────────────────────────────────────────

    private void handleYes() {
        // TODO Phase 7.2 — save vote to Firebase, then advance card
        advanceCard();
    }

    private void handleNo() {
        // TODO Phase 7.2 — discard and advance card
        advanceCard();
    }

    /** Move ViewPager2 to the next card with a smooth scroll. */
    private void advanceCard() {
        int next = viewPagerMovies.getCurrentItem() + 1;
        if (next < movieCardAdapter.getItemCount()) {
            viewPagerMovies.setCurrentItem(next, true);
        }
    }

    // ── TMDB fetch ─────────────────────────────────────────────────────────────

    /**
     * Fetches trending movies (day) from TMDB.
     *
     * The page number is derived deterministically from the room code so that
     * every device in the same lobby always hits the same page — guaranteeing
     * an identical, ordered movie list for all users without any server coordination.
     *
     * Different room codes → different pages (1-100) → session variety.
     * No shuffling — TMDB's ordering is preserved as-is.
     */
    private void fetchMovies() {
        // Derive a stable page number (1–100) from the room code.
        // Math.abs guards against negative hashCode values.
        int page = (roomCode != null && !roomCode.isEmpty())
                ? (Math.abs(roomCode.hashCode()) % 100) + 1
                : 1; // fallback for solo/test sessions

        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;

        TmdbApiClient.getService()
                .getTrendingMovies("day", "en-US", page, bearer)
                .enqueue(new Callback<MovieListResponse>() {
                    @Override
                    public void onResponse(Call<MovieListResponse> call,
                                           Response<MovieListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null && !movies.isEmpty()) {
                                movieCardAdapter.setMovies(movies);
                                Logger.d(TAG, "Loaded " + movies.size()
                                        + " movies (page " + page + ")");
                            }
                        } else {
                            Logger.w(TAG, "TMDB fetch failed: HTTP " + response.code());
                            Toast.makeText(SwipingActivity.this,
                                    "Could not load movies. Check your connection.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieListResponse> call, Throwable t) {
                        Logger.e(TAG, "TMDB fetch error: " + t.getMessage());
                        Toast.makeText(SwipingActivity.this,
                                "Network error. Please check your connection.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

