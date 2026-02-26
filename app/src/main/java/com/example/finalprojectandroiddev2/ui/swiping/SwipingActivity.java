package com.example.finalprojectandroiddev2.ui.swiping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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

    /** Optional extra: override the initial TMDB page for this swiping session. */
    public static final String EXTRA_INITIAL_PAGE = "initial_page";

    private ViewPager2                       viewPagerMovies;
    private MovieCardAdapter                 movieCardAdapter;
    private View                             btnYes;
    private TextView                         tvMemberStatus;
    private View                             layoutSwipeControls;
    private View                             btnExitSession;
    private String                           roomCode;
    private String                           currentUserId;
    private FirebaseRepository               firebaseRepo;
    private List<Movie>                      currentMovies;
    private Map<String, LobbyMember>         memberMap = new HashMap<>();
    private boolean                          isHost    = false;
    private int                              currentPage = 0;
    /** Tracks whether the first batch of movies has been loaded (setMovies vs appendMovies). */
    private boolean                          initialLoadDone = false;

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

        // Read isHost from Intent — available immediately, no async wait needed.
        isHost = getIntent().getBooleanExtra(LobbyActivity.EXTRA_IS_HOST, false);

        // Optional override for initial TMDB page (used when host taps "Find Another Match").
        int overrideInitialPage = getIntent().getIntExtra(EXTRA_INITIAL_PAGE, -1);

        // Disable back press during a swiping session — users should not be able to
        // accidentally abandon an active lobby. Works on all API levels including
        // Android 13+ predictive back gestures.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Intentionally empty — back is disabled during an active swiping session.
            }
        });

        bindViews();
        setupViewPager();
        setupButtons();

        // ── Firebase-driven movie loading ────────────────────────────────────
        // ALL devices listen for currentPage changes via listenForPageChanges().
        // Members react to every change by fetching the TMDB page.
        // The HOST fetches locally (in onCreate + loadMoreMovies) and writes to
        // Firebase so members' listeners fire.
        if (roomCode != null && !roomCode.isEmpty()) {
            listenForPageChanges();

            if (isHost) {
                // Determine the initial page:
                // - If coming from "Find Another Match", use the override page.
                // - Otherwise, derive a deterministic page from the room code hash.
                int initialPage;
                if (overrideInitialPage > 0) {
                    initialPage = overrideInitialPage;
                } else {
                    initialPage = (Math.abs(roomCode.hashCode()) % 100) + 1;
                }

                currentPage = initialPage;

                // Host fetches movies locally (no dependency on Firebase callback)
                fetchMoviesForPage(initialPage);

                // Broadcast to Firebase so member listeners fire
                firebaseRepo.setCurrentPage(roomCode, initialPage);
            }
        }

        listenForMatch();
        loadMembersAndStartVoteSync();
    }

    // ── Views ──────────────────────────────────────────────────────────────────

    private void bindViews() {
        viewPagerMovies     = findViewById(R.id.viewpager_movies);
        btnYes              = findViewById(R.id.btn_swipe_yes);
        tvMemberStatus      = findViewById(R.id.text_member_status);
        layoutSwipeControls = findViewById(R.id.layout_swipe_controls);
        btnExitSession      = findViewById(R.id.btn_exit_session);
    }

    // ── ViewPager2 setup ───────────────────────────────────────────────────────

    private void setupViewPager() {
        movieCardAdapter = new MovieCardAdapter();
        viewPagerMovies.setAdapter(movieCardAdapter);

        // Route swipe gestures through the same Yes/No handlers as the buttons.
        movieCardAdapter.setSwipeCallback(new MovieCardAdapter.SwipeCallback() {
            @Override public void onSwipedYes() { handleYes(); }
            @Override public void onSwipedNo()  { handleNo();  }
        });

        // End-of-deck card: host gets Load More, members get waiting hint.
        // The callback wires the button click back to loadMoreMovies().
        movieCardAdapter.setEndOfDeckCallback(() -> loadMoreMovies());
        // isHost is set later in loadMembersAndStartVoteSync(); call refreshEndOfDeckCard()
        // after host status is resolved so the adapter re-binds the end card correctly.

        // Disable ViewPager2's own swipe — cards advance only via gesture on card or buttons.
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

        // Re-attach the vote listener for the current card on page change.
        viewPagerMovies.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Only sync votes for real movie cards (not the end-of-deck card)
                if (currentMovies != null && position < currentMovies.size()) {
                    int movieId = currentMovies.get(position).getId();
                    if (roomCode != null && !roomCode.isEmpty()) {
                        attachVoteSyncForMovie(movieId);
                    }
                    layoutSwipeControls.setVisibility(View.VISIBLE);
                    btnExitSession.setVisibility(View.VISIBLE);
                } else {
                    layoutSwipeControls.setVisibility(View.INVISIBLE);
                    btnExitSession.setVisibility(View.INVISIBLE);
                }
                // Note: NO showEndOfDeck() call here.
                // The end-of-deck card appears naturally when advanceCard() moves
                // past the last real movie to position movies.size().
            }
        });
    }

    // ── Buttons ────────────────────────────────────────────────────────────────

    private void setupButtons() {
        // Yes / No buttons
        findViewById(R.id.btn_swipe_yes).setOnClickListener(v -> handleYes());
        findViewById(R.id.btn_swipe_no).setOnClickListener(v  -> handleNo());

        // Exit session
        btnExitSession.setOnClickListener(v -> {
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

    // ── End of Deck (Phase 7.5) ─ Logic lives in MovieCardAdapter.EndOfDeckViewHolder ──────
    // The end-of-deck card is always the last slot in the adapter (position = movies.size()).
    // It becomes accessible once advanceCard() scrolls past the final real movie.
    // No overlay visibility management is needed here — it’s all in the ViewHolder bind().

    /**
     * Host-only: fetch the next TMDB page, append unique movies to the adapter,
     * broadcast the page number via Firebase, and hide the overlay.
     */
    private void loadMoreMovies() {
        currentPage++;
        Logger.d(TAG, "loadMoreMovies → fetching page " + currentPage);

        // 1. Host fetches movies locally (guaranteed to work — no Firebase dependency)
        fetchMoviesForPage(currentPage);

        // 2. Broadcast the new page to Firebase so members' listeners fire
        if (roomCode != null && !roomCode.isEmpty()) {
            firebaseRepo.setCurrentPage(roomCode, currentPage);
        }
    }

    /**
     * Unified page listener — handles BOTH the initial movie load AND load-more.
     * Firebase {@code currentPage} is the single source of truth.
     *
     * Flow:
     *  1. LobbyActivity resets currentPage to 0 before starting the session.
     *  2. Host writes initialPage to Firebase in onCreate().
     *  3. This listener fires on ALL devices with the new page → TMDB fetch.
     *  4. Host clicks "Load More" → writes currentPage+1 → listener fires again.
     *
     * Guards: page <= 0 (sentinel/invalid) and page == currentPage (echo/duplicate).
     */
    private void listenForPageChanges() {
        if (roomCode == null || roomCode.isEmpty()) return;
        firebaseRepo.listenCurrentPage(roomCode, page -> {
            Logger.d(TAG, "listenForPageChanges → received page=" + page
                    + ", currentPage=" + currentPage + ", isHost=" + isHost);

            // Skip sentinel (0) and any echo of a value we already processed
            if (page <= 0 || page == currentPage) return;

            // Host already fetched in loadMoreMovies() — just update tracking
            if (isHost) {
                currentPage = page;
                return;
            }

            // Member: update tracking and fetch movies
            currentPage = page;
            fetchMoviesForPage(page);
        });
    }

    /**
     * Fetches trending movies for the given TMDB page.
     * First call uses setMovies() (replaces adapter); subsequent calls use appendMovies().
     * Called by listenForPageChanges() on ALL devices (host + member) identically.
     */
    private void fetchMoviesForPage(int page) {
        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiClient.getService()
                .getTrendingMovies("day", "en-US", page, bearer)
                .enqueue(new Callback<MovieListResponse>() {
                    @Override
                    public void onResponse(Call<MovieListResponse> call,
                                           Response<MovieListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            runOnUiThread(() -> {
                                if (!initialLoadDone) {
                                    // ── First load: replace the entire adapter ────────
                                    currentMovies = movies;
                                    movieCardAdapter.setMovies(movies);
                                    initialLoadDone = true;
                                    Logger.d(TAG, "Initial load: " + movies.size()
                                            + " movies (page " + page + ")");
                                    // Kick off vote sync if memberMap is ready
                                    if (!memberMap.isEmpty() && !movies.isEmpty()) {
                                        attachVoteSyncForMovie(movies.get(0).getId());
                                    }
                                } else {
                                    // ── Load more: append to existing deck ────────────
                                    int firstNewPos = currentMovies.size();
                                    int added = movieCardAdapter.appendMovies(movies);
                                    if (movies != null) currentMovies.addAll(movies);
                                    Logger.d(TAG, "Appended " + added
                                            + " movies (page " + page + ")");
                                    if (added > 0) {
                                        viewPagerMovies.post(() ->
                                                viewPagerMovies.setCurrentItem(firstNewPos, true));
                                    }
                                }
                            });
                        } else {
                            Logger.w(TAG, "TMDB fetch failed: HTTP " + response.code()
                                    + " (page " + page + ")");
                            runOnUiThread(() ->
                                    Toast.makeText(SwipingActivity.this,
                                            "Could not load movies. Try again.",
                                            Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieListResponse> call, Throwable t) {
                        Logger.e(TAG, "TMDB fetch error (page " + page + "): " + t.getMessage());
                        runOnUiThread(() ->
                                Toast.makeText(SwipingActivity.this,
                                        "Network error. Please try again.",
                                        Toast.LENGTH_SHORT).show());
                    }
                });
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
            // isHost is already set from Intent; just update the adapter.
            movieCardAdapter.setIsHost(isHost);
            // Start listening to votes for card 0 (if movies are already loaded)
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
        intent.putExtra(LobbyActivity.EXTRA_IS_HOST, isHost);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detach only swiping-owned listeners (votes + page). Do not detach status here,
        // because MatchActivity may attach a status listener while this activity is finishing.
        firebaseRepo.detachSwipingListeners();
    }

    // ── TMDB fetch (fetchMoviesForPage) is defined above, near listenForPageChanges ──
}
