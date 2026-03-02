package com.example.finalprojectandroiddev2.ui.movies;

import android.content.Intent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.finalprojectandroiddev2.ui.home.PopularMovieAdapter;
import com.example.finalprojectandroiddev2.ui.home.TopRatedMovieAdapter;
import com.example.finalprojectandroiddev2.ui.home.TrendingMovieAdapter;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesActivity extends BaseActivity {

    private TrendingMovieAdapter trendingAdapter;
    private TopRatedMovieAdapter topRatedAdapter;
    private PopularMovieAdapter popularAdapter;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        applyEdgeToEdgeInsets(R.id.container_movies);

        drawerLayout = findViewById(R.id.drawer_layout);

        // Navbar menu button toggles sidebar
        findViewById(R.id.btn_menu).setOnClickListener(v -> openSidebar());

        // Highlight "Movies" instead of "Home" in the sidebar
        TextView btnHome = findViewById(R.id.sidebar_btn_home);
        if (btnHome != null) {
            btnHome.setTextColor(ContextCompat.getColor(this, R.color.color_text_secondary));
        }
        TextView btnMovies = findViewById(R.id.sidebar_btn_movies);
        if (btnMovies != null) {
            btnMovies.setTextColor(ContextCompat.getColor(this, R.color.color_primary));
        }

        // Sidebar buttons
        findViewById(R.id.sidebar_btn_close).setOnClickListener(v -> closeSidebar());
        
        findViewById(R.id.sidebar_btn_home).setOnClickListener(v -> {
            closeSidebar();
            // Go back to HomeActivity (finish assuming Home is on top of back stack)
            finish();
        });

        findViewById(R.id.sidebar_btn_library).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_library_coming_soon, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.sidebar_btn_about).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_about_coming_soon, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.sidebar_btn_team).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_team_coming_soon, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.sidebar_btn_movies).setOnClickListener(v -> {
            closeSidebar(); // Already in Movies
        });

        findViewById(R.id.sidebar_btn_signout).setOnClickListener(v -> logout());

        findViewById(R.id.sidebar_user_profile).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_profile_coming_soon, Toast.LENGTH_SHORT).show();
        });

        // Back-press: close drawer if open, else finish activity
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(android.view.Gravity.END)) {
                    closeSidebar();
                } else {
                    finish();
                }
            }
        });

        loadUserProfile();

        // "See More" buttons
        findViewById(R.id.btn_trending_see_more).setOnClickListener(v -> {
            Intent intent = new Intent(this, MovieCategoryActivity.class);
            intent.putExtra(MovieCategoryActivity.EXTRA_CATEGORY, MovieCategoryActivity.CATEGORY_TRENDING);
            startActivity(intent);
        });
        findViewById(R.id.btn_top_rated_see_more).setOnClickListener(v -> {
            Intent intent = new Intent(this, MovieCategoryActivity.class);
            intent.putExtra(MovieCategoryActivity.EXTRA_CATEGORY, MovieCategoryActivity.CATEGORY_TOP_RATED);
            startActivity(intent);
        });
        findViewById(R.id.btn_popular_see_more_page).setOnClickListener(v -> {
            Intent intent = new Intent(this, MovieCategoryActivity.class);
            intent.putExtra(MovieCategoryActivity.EXTRA_CATEGORY, MovieCategoryActivity.CATEGORY_POPULAR);
            startActivity(intent);
        });

        // Search field
        TextInputLayout inputLayoutSearch = findViewById(R.id.input_search_movies);
        EditText searchEdit = findViewById(R.id.edit_search_movies);
        TextView textSearchError = findViewById(R.id.text_search_error);

        // Handle keyboard enter / search action
        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchEdit.getText().toString().trim(), textSearchError);
                return true;
            }
            return false;
        });

        // Handle end icon click (the search icon)
        inputLayoutSearch.setEndIconOnClickListener(v -> {
            performSearch(searchEdit.getText().toString().trim(), textSearchError);
        });

        setupTrendingMovies();
        setupTopRatedMovies();
        setupPopularMovies();
    }

    private long lastSearchTime = 0;

    private void performSearch(String query, TextView errorText) {
        if (query.isEmpty()) {
            errorText.setText(R.string.error_search_query_required);
            errorText.setVisibility(View.VISIBLE);
        } else {
            // Prevent double-launching the activity if triggered rapidly (e.g., keyboard ACTION_DOWN / ACTION_UP)
            if (System.currentTimeMillis() - lastSearchTime < 1000) {
                return;
            }
            lastSearchTime = System.currentTimeMillis();

            errorText.setVisibility(View.GONE);
            Intent intent = new Intent(this, SearchedMovieResultActivity.class);
            intent.putExtra(SearchedMovieResultActivity.EXTRA_QUERY, query);
            startActivity(intent);
        }
    }

    private void showComingSoon() {
        Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
    }

    // ── Trending Movies ───────────────────────────────────────────────────────
    private void setupTrendingMovies() {
        RecyclerView rv = findViewById(R.id.rv_trending_movies_page);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        trendingAdapter = new TrendingMovieAdapter(new ArrayList<>());
        rv.setAdapter(trendingAdapter);

        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiService api = TmdbApiClient.getService();
        api.getTrendingMovies("day", "en-US", 18, bearer)
                .enqueue(new Callback<MovieListResponse>() {
                    @Override
                    public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null && movies.size() > 1) {
                                List<Movie> reordered = new ArrayList<>(movies.subList(1, movies.size()));
                                reordered.addAll(movies.subList(0, 1));
                                trendingAdapter.setMovies(reordered);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieListResponse> call, Throwable t) {
                        Toast.makeText(MoviesActivity.this, "Failed to load trending movies", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Top Rated Movies ──────────────────────────────────────────────────
    private void setupTopRatedMovies() {
        RecyclerView rv = findViewById(R.id.rv_top_rated_movies_page);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        topRatedAdapter = new TopRatedMovieAdapter(new ArrayList<>());
        rv.setAdapter(topRatedAdapter);

        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiClient.getService().getTopRatedMovies("en-US", 48, bearer)
                .enqueue(new Callback<MovieListResponse>() {
                    @Override
                    public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null && movies.size() > 9) {
                                List<Movie> reordered = new ArrayList<>(movies.subList(9, movies.size()));
                                reordered.addAll(movies.subList(0, 9));
                                topRatedAdapter.setMovies(reordered);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieListResponse> call, Throwable t) {
                        Toast.makeText(MoviesActivity.this, "Failed to load top rated movies", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Popular Movies ────────────────────────────────────────────────────────
    private void setupPopularMovies() {
        RecyclerView rv = findViewById(R.id.rv_popular_movies_page);
        rv.setLayoutManager(new LinearLayoutManager(this));

        popularAdapter = new PopularMovieAdapter(new ArrayList<>());
        rv.setAdapter(popularAdapter);

        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiClient.getService().getPopularMovies("en-US", 36, bearer)
                .enqueue(new Callback<MovieListResponse>() {
                    @Override
                    public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            if (movies != null && movies.size() > 15) {
                                List<Movie> reordered = new ArrayList<>(movies.subList(15, movies.size()));
                                reordered.addAll(movies.subList(0, 15));
                                popularAdapter.setMovies(reordered);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieListResponse> call, Throwable t) {
                        Toast.makeText(MoviesActivity.this, "Failed to load popular movies", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Drawer & Profile Logic ────────────────────────────────────────────────
    private void openSidebar() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(android.view.Gravity.END);
        }
    }

    private void closeSidebar() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(android.view.Gravity.END);
        }
    }

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

    private void logout() {
        AuthRepository.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
