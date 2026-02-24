package com.example.finalprojectandroiddev2.ui.swiping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalprojectandroiddev2.BuildConfig;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.api.TmdbApiClient;
import com.example.finalprojectandroiddev2.data.model.LobbyMember;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.data.model.MovieListResponse;
import com.example.finalprojectandroiddev2.data.repository.FirebaseRepository;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.lobby.LobbyActivity;
import com.example.finalprojectandroiddev2.ui.match.MatchActivity;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.LobbyPrefs;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private ViewPager2                       viewPagerMovies;
    private MovieCardAdapter                 movieCardAdapter;
    private View                             btnYes;
    private TextView                         tvMemberStatus;
    private String                           roomCode;
    private String                           currentUserId;
    private FirebaseRepository               firebaseRepo;
    private List<Movie>                      currentMovies;
    private Map<String, LobbyMember>         memberMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiping);
        applyEdgeToEdgeInsets(R.id.container_swiping);

        // Session has started — clear the active lobby so the "Tap to return to lobby"
        // banner never appears on the Home screen while/after swiping.
        LobbyPrefs.clearActiveRoomCode(this);

        roomCode      = getIntent().getStringExtra(LobbyActivity.EXTRA_ROOM_CODE);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        firebaseRepo  = FirebaseRepository.getInstance();

        bindViews();
        setupViewPager();
        setupButtons();
        fetchMovies();
        listenForMatch();
        loadMembersAndStartVoteSync();
    }

    // ── Views ──────────────────────────────────────────────────────────────────

    private void bindViews() {
        viewPagerMovies = findViewById(R.id.viewpager_movies);
        btnYes          = findViewById(R.id.btn_swipe_yes);
        tvMemberStatus  = findViewById(R.id.text_member_status);
    }

    // ── ViewPager2 setup ───────────────────────────────────────────────────────

    private void setupViewPager() {
        movieCardAdapter = new MovieCardAdapter();
        viewPagerMovies.setAdapter(movieCardAdapter);

        // Route swipe gestures through the same Yes/No handlers as the buttons.
        // This ensures both input methods (swipe gesture + button tap) are unified
        // and will both trigger vote recording in Phase 7.2.
        movieCardAdapter.setSwipeCallback(new MovieCardAdapter.SwipeCallback() {
            @Override public void onSwipedYes() { handleYes(); }
            @Override public void onSwipedNo()  { handleNo();  }
        });

        // Disable ViewPager2's own swipe — cards only advance via gesture on the card
        // itself (routed through SwipeCallback above) or via the Yes/No buttons.
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

        // Re-attach the vote listener whenever the user navigates to a new card.
        viewPagerMovies.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (currentMovies != null && position < currentMovies.size()) {
                    int movieId = currentMovies.get(position).getId();
                    if (roomCode != null && !roomCode.isEmpty()) {
                        attachVoteSyncForMovie(movieId);
                    }
                }
            }
        });
    }

    // ── Buttons ────────────────────────────────────────────────────────────────

    private void setupButtons() {
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

    // ── Yes / No vote logic (Phase 7.3) ──────────────────────────────────────────

    /**
     * Called when the user votes Yes (button tap OR swipe-right gesture).
     * Writes the vote to Firebase, then advances to the next card.
     * If this vote triggers a unanimous match, listenForMatch() handles navigation.
     */
    private void handleYes() {
        int position = viewPagerMovies.getCurrentItem();
        if (currentMovies == null || position >= currentMovies.size()) {
            advanceCard();
            return;
        }
        Movie movie   = currentMovies.get(position);
        int   movieId = movie.getId();

        // Disable button briefly to prevent duplicate taps
        btnYes.setEnabled(false);

        if (roomCode != null && !roomCode.isEmpty() && !currentUserId.isEmpty()) {
            firebaseRepo.recordVote(roomCode, currentUserId, movieId,
                    new FirebaseRepository.VoteCallback() {
                        @Override public void onVoteRecorded() {
                            Logger.d(TAG, "Yes vote written for movie " + movieId);
                            runOnUiThread(() -> {
                                btnYes.setEnabled(true);
                                advanceCard();
                            });
                        }
                        @Override public void onMatchFound(int matchedMovieId) {
                            // Status listener (listenForMatch) will fire and handle
                            // navigation for ALL devices — including this one.
                            Logger.d(TAG, "Match found (via vote): movie " + matchedMovieId);
                        }
                        @Override public void onError(String message) {
                            Logger.e(TAG, "Vote error: " + message);
                            runOnUiThread(() -> {
                                btnYes.setEnabled(true);
                                Toast.makeText(SwipingActivity.this,
                                        "Vote failed. Try again.", Toast.LENGTH_SHORT).show();
                                advanceCard(); // still advance so the session isn't stuck
                            });
                        }
                    });
        } else {
            // Solo / test session (no room code) — just advance
            btnYes.setEnabled(true);
            advanceCard();
        }
    }

    /**
     * Called when the user votes No (button tap OR swipe-left gesture).
     * No votes are intentionally NOT saved to Firebase. Card is simply advanced.
     */
    private void handleNo() {
        advanceCard();
    }

    /** Move ViewPager2 to the next card with a smooth scroll. */
    private void advanceCard() {
        int next = viewPagerMovies.getCurrentItem() + 1;
        if (next < movieCardAdapter.getItemCount()) {
            viewPagerMovies.setCurrentItem(next, true);
        }
    }

    // ── Real-time vote sync (Phase 7.4) ──────────────────────────────────────────

    /**
     * Loads lobby member map (userId→username) once, then attaches the vote listener
     * for the first card. Called during onCreate so the status bar is ready immediately.
     */
    private void loadMembersAndStartVoteSync() {
        if (roomCode == null || roomCode.isEmpty()) {
            tvMemberStatus.setText("Solo session");
            return;
        }
        firebaseRepo.loadAllMembers(roomCode, members -> {
            memberMap = members;
            // Start listening to votes for card 0 (the first movie shown)
            if (currentMovies != null && !currentMovies.isEmpty()) {
                attachVoteSyncForMovie(currentMovies.get(0).getId());
            }
        });
    }

    /**
     * Attaches (or re-attaches) the real-time vote listener for a specific movie.
     * Called both after members load and on every onPageSelected event.
     */
    private void attachVoteSyncForMovie(int movieId) {
        if (roomCode == null || roomCode.isEmpty()) return;
        firebaseRepo.listenVotesForMovie(roomCode, movieId, voterUids ->
                runOnUiThread(() -> updateVoteStatusBar(voterUids, memberMap.size()))
        );
    }

    /**
     * Updates the member status bar to show who has voted Yes on the current movie.
     *
     * Format: "James ✓  · You ✓  ·  1 / 3 voted"
     * Current user is shown as "You" for clarity. Unvoted members are not shown.
     * If nobody has voted yet, shows "Waiting for votes…".
     */
    private void updateVoteStatusBar(Set<String> voterUids, int totalMembers) {
        if (voterUids.isEmpty()) {
            tvMemberStatus.setText("Waiting for votes…");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String uid : voterUids) {
            if (sb.length() > 0) sb.append("  ·  ");
            if (uid.equals(currentUserId)) {
                sb.append("You ✓");
            } else {
                LobbyMember m = memberMap.get(uid);
                String name = (m != null && m.getUsername() != null)
                        ? m.getUsername() : "Member";
                sb.append(name).append(" ✓");
            }
        }
        if (totalMembers > 0) {
            sb.append("  ·  ").append(voterUids.size()).append("/").append(totalMembers).append(" voted");
        }
        tvMemberStatus.setText(sb.toString());
    }


    // ── Match listener ────────────────────────────────────────────────────────────

    /**
     * Listens to lobbies/{roomCode}/status changes.
     * When status becomes "matched" → navigates ALL devices to MatchActivity.
     * Single source of truth for match navigation — every device transitions together.
     */
    private void listenForMatch() {
        if (roomCode == null || roomCode.isEmpty()) return;
        firebaseRepo.listenLobbyStatus(roomCode, status -> {
            if (Constants.LOBBY_STATUS_MATCHED.equals(status)) {
                runOnUiThread(this::navigateToMatch);
            }
        });
    }

    private void navigateToMatch() {
        if (isFinishing() || isDestroyed()) return;
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra(LobbyActivity.EXTRA_ROOM_CODE, roomCode);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseRepo.detachListeners();
    }

    // ── TMDB fetch ─────────────────────────────────────────────────────────────

    /**
     * Fetches trending movies (day) from TMDB.
     * Page is seeded by room code for session variety and identical decks.
     */
    private void fetchMovies() {
        int page = (roomCode != null && !roomCode.isEmpty())
                ? (Math.abs(roomCode.hashCode()) % 100) + 1
                : 1;

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
                                currentMovies = movies;
                                movieCardAdapter.setMovies(movies);
                                Logger.d(TAG, "Loaded " + movies.size()
                                        + " movies (page " + page + ")");
                                // If member map already loaded, kick off vote sync now.
                                // (Handles the case where members loaded before movies.)
                                if (!memberMap.isEmpty()) {
                                    attachVoteSyncForMovie(movies.get(0).getId());
                                }
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
