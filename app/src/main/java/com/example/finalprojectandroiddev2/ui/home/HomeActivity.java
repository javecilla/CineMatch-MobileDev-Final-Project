package com.example.finalprojectandroiddev2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroiddev2.data.model.Movie;

import java.util.ArrayList;
import java.util.List;

import com.example.finalprojectandroiddev2.BuildConfig;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.api.TmdbApiClient;
import com.example.finalprojectandroiddev2.data.api.TmdbApiService;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.data.model.MovieListResponse;
import com.example.finalprojectandroiddev2.data.repository.AuthRepository;
import com.example.finalprojectandroiddev2.data.repository.UserRepository;
import com.example.finalprojectandroiddev2.model.UserProfile;
import com.example.finalprojectandroiddev2.ui.auth.LoginActivity;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.lobby.CreateLobbyActivity;
import com.example.finalprojectandroiddev2.ui.lobby.JoinLobbyActivity;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Home screen.
 * - Hamburger menu button slides in sidebar from the END (right) edge.
 * - Sidebar: close button, nav items (Home / Favorites / Watchlist / Profile),
 *   user avatar + name + email, Sign Out at the bottom.
 * - Double back-press exits app; single back-press closes drawer if open.
 */
public class HomeActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private long lastBackPressMs = 0;
    private TrendingMovieAdapter trendingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        applyEdgeToEdgeInsets(R.id.container_home);

        drawerLayout = findViewById(R.id.drawer_layout);

        // ── Navbar ────────────────────────────────────────────────────────────
        findViewById(R.id.btn_menu).setOnClickListener(v -> openSidebar());

        // ── Main content ──────────────────────────────────────────────────────
        findViewById(R.id.btn_create_lobby).setOnClickListener(v ->
                startActivity(new Intent(this, CreateLobbyActivity.class)));

        findViewById(R.id.btn_join_lobby).setOnClickListener(v ->
                startActivity(new Intent(this, JoinLobbyActivity.class)));

        // ── Sidebar buttons ───────────────────────────────────────────────────
        findViewById(R.id.sidebar_btn_close).setOnClickListener(v -> closeSidebar());

        findViewById(R.id.sidebar_btn_home).setOnClickListener(v -> closeSidebar());

        findViewById(R.id.sidebar_btn_favorites).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_favorites_coming_soon, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.sidebar_btn_watchlist).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_watchlist_coming_soon, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.sidebar_btn_movies).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_movies_coming_soon, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.sidebar_btn_signout).setOnClickListener(v -> logout());

        // ──  User Profile ────────────────────────────────────────────────────
        findViewById(R.id.sidebar_user_profile).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_profile_coming_soon, Toast.LENGTH_SHORT).show();
        });

        // ── Back-press: close drawer first, double-tap to exit ────────────────
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(android.view.Gravity.END)) {
                    closeSidebar();
                    return;
                }
                long now = System.currentTimeMillis();
                if (now - lastBackPressMs < 2000) {
                    finishAffinity();
                } else {
                    lastBackPressMs = now;
                    Toast.makeText(HomeActivity.this,
                            R.string.msg_press_back_again_to_exit, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadUserProfile();
        setupTrendingMovies();
    }

    // ── Trending Movies ───────────────────────────────────────────────────────

    /**
     * Sets up the horizontal carousel and immediately fetches trending movies from
     * TMDB /trending/movie/day using the Bearer read-access token from BuildConfig.
     */
    private void setupTrendingMovies() {
        RecyclerView rv = findViewById(R.id.rv_trending_movies);
        rv.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));

        trendingAdapter = new TrendingMovieAdapter(new ArrayList<>());
        rv.setAdapter(trendingAdapter);

        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;

        TmdbApiService api = TmdbApiClient.getService();
        api.getTrendingMovies("day", "en-US", bearer)
                .enqueue(new Callback<MovieListResponse>() {
                    @Override
                    public void onResponse(Call<MovieListResponse> call,
                                          Response<MovieListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null && !movies.isEmpty()) {
                                trendingAdapter.setMovies(movies);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieListResponse> call, Throwable t) {
                        Toast.makeText(HomeActivity.this,
                                "Failed to load trending movies",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Drawer helpers ────────────────────────────────────────────────────────

    private void openSidebar() {
        drawerLayout.openDrawer(android.view.Gravity.END);
    }

    private void closeSidebar() {
        drawerLayout.closeDrawer(android.view.Gravity.END);
    }

    // ── Profile ───────────────────────────────────────────────────────────────

    private void loadUserProfile() {
        FirebaseUser firebaseUser = AuthRepository.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        String email = firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "";
        setSidebarEmail(email);

        UserRepository.getInstance().getUserProfile(firebaseUser.getUid(),
                new UserRepository.ProfileLoadCallback() {
                    @Override
                    public void onSuccess(UserProfile profile) {
                        if (profile != null && profile.getName() != null && !profile.getName().isEmpty()) {
                            setSidebarUsername(profile.getName());
                        } else {
                            setSidebarUsername(email.isEmpty()
                                    ? getString(R.string.sidebar_default_username) : email);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setSidebarUsername(email.isEmpty()
                                ? getString(R.string.sidebar_default_username) : email);
                    }
                });
    }

    private void setSidebarUsername(String name) {
        TextView tv = findViewById(R.id.sidebar_text_username);
        if (tv != null) tv.setText(name);
    }

    private void setSidebarEmail(String email) {
        TextView tv = findViewById(R.id.sidebar_text_email);
        if (tv != null) tv.setText(email);
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    private void logout() {
        AuthRepository.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
