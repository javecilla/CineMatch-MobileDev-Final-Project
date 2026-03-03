package com.example.finalprojectandroiddev2.ui.movies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
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
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.home.TopRatedMovieAdapter;
import com.example.finalprojectandroiddev2.ui.home.TrendingMovieAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewMovieActivity extends BaseActivity {

    public static final String EXTRA_MOVIE_ID = "extra_movie_id";

    private DrawerLayout drawerLayout;

    // UI Elements
    private ImageView ivBackdrop;
    private TextView tvTitle;
    private TextView tvReleaseDate;
    private TextView tvRating;
    private TextView tvOverview;
    private ChipGroup chipGroupGenres;

    private RecyclerView rvSimilar;
    private RecyclerView rvRecommended;
    private ProgressBar pbSimilar;
    private ProgressBar pbRecommended;

    // Adapters
    private TopRatedMovieAdapter similarAdapter;
    private TrendingMovieAdapter recommendedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie);
        applyEdgeToEdgeInsets(R.id.container_view_movie);

        drawerLayout = findViewById(R.id.drawer_layout);

        // Map Views
        ivBackdrop = findViewById(R.id.iv_movie_backdrop);
        tvTitle = findViewById(R.id.text_movie_title);
        tvReleaseDate = findViewById(R.id.text_movie_release_date);
        tvRating = findViewById(R.id.text_movie_rating);
        tvOverview = findViewById(R.id.text_movie_overview);
        chipGroupGenres = findViewById(R.id.chip_group_genres);

        rvSimilar = findViewById(R.id.rv_similar_movies);
        rvRecommended = findViewById(R.id.rv_recommended_movies);
        pbSimilar = findViewById(R.id.pb_similar_movies);
        pbRecommended = findViewById(R.id.pb_recommended_movies);

        // Handle navigation buttons
        findViewById(R.id.btn_menu).setOnClickListener(v -> openSidebar());
        findViewById(R.id.btn_back_view_movie).setOnClickListener(v -> closeOrFinish());

        // Back-press logic
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                closeOrFinish();
            }
        });

        setupSidebar();
        setupRecyclerViews();

        // Load Movie Details
        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        if (movieId != -1) {
            fetchMovieDetails(movieId);
            fetchSimilarMovies(movieId);
            fetchRecommendedMovies(movieId);
        } else {
            Toast.makeText(this, "Movie ID missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerViews() {
        rvSimilar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        similarAdapter = new TopRatedMovieAdapter(new ArrayList<>(), movie -> {
            MovieModalBottomSheet modal = MovieModalBottomSheet.newInstance(movie.getId());
            modal.show(getSupportFragmentManager(), MovieModalBottomSheet.TAG);
        });
        rvSimilar.setAdapter(similarAdapter);

        rvRecommended.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recommendedAdapter = new TrendingMovieAdapter(new ArrayList<>(), movie -> {
            MovieModalBottomSheet modal = MovieModalBottomSheet.newInstance(movie.getId());
            modal.show(getSupportFragmentManager(), MovieModalBottomSheet.TAG);
        });
        // TrendingMovieAdapter might not have a public setOnMovieLongClickListener, 
        // We'll skip adding a secondary long click listener here if it's missing, 
        // but let's try setting it anyway or remove it if it causes errors.
        // ACTUALLY the error says "cannot find symbol setOnMovieLongClickListener" 
        // so we must remove it from trending adapter
        rvRecommended.setAdapter(recommendedAdapter);
    }

    private void fetchMovieDetails(int movieId) {
        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiClient.getService().getMovieDetails(movieId, "en-US", bearer)
                .enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            populateMovieDetails(response.body());
                        } else {
                            Toast.makeText(ViewMovieActivity.this, "Details failed to load", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        Toast.makeText(ViewMovieActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateMovieDetails(Movie movie) {
        tvTitle.setText(movie.getTitle() != null ? movie.getTitle() : "Unknown Movie");
        tvOverview.setText(movie.getOverview() != null && !movie.getOverview().isEmpty()
                ? movie.getOverview() : "No overview available.");

        if (movie.getReleaseDate() != null && movie.getReleaseDate().length() >= 4) {
            tvReleaseDate.setText(movie.getReleaseDate().substring(0, 4));
        } else {
            tvReleaseDate.setText("-");
        }

        tvRating.setText(String.format("%.1f", movie.getVoteAverage()));

        if (movie.getBackdropPath() != null) {
            Glide.with(this)
                 .load("https://image.tmdb.org/t/p/w1280" + movie.getBackdropPath())
                 .centerCrop()
                 .into(ivBackdrop);
        } else if (movie.getPosterPath() != null) {
            Glide.with(this)
                 .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                 .centerCrop()
                 .into(ivBackdrop);
        }

        // Add Genre Chips
        chipGroupGenres.removeAllViews();
        if (movie.getGenres() != null) {
            for (Movie.Genre genre : movie.getGenres()) {
                Chip chip = new Chip(this);
                chip.setText(genre.getName());
                chip.setChipBackgroundColorResource(R.color.color_background); // fallback
                chip.setTextColor(ContextCompat.getColor(this, R.color.color_text_primary));
                chip.setChipStrokeColorResource(R.color.color_primary);
                chip.setChipStrokeWidth(2f);
                chipGroupGenres.addView(chip);
            }
        }
    }

    private void fetchSimilarMovies(int movieId) {
        pbSimilar.setVisibility(View.VISIBLE);
        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiClient.getService().getSimilarMovies(movieId, "en-US", 1, bearer)
                .enqueue(new Callback<MovieListResponse>() {
                    @Override
                    public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                        pbSimilar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> results = response.body().getResults();
                            if (results != null) {
                                similarAdapter.setMovies(results); 
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieListResponse> call, Throwable t) {
                        pbSimilar.setVisibility(View.GONE);
                    }
                });
    }

    private void fetchRecommendedMovies(int movieId) {
        pbRecommended.setVisibility(View.VISIBLE);
        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiClient.getService().getRecommendedMovies(movieId, "en-US", 1, bearer)
                .enqueue(new Callback<MovieListResponse>() {
                    @Override
                    public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                        pbRecommended.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Movie> results = response.body().getResults();
                            if (results != null) {
                                recommendedAdapter.setMovies(results); 
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieListResponse> call, Throwable t) {
                        pbRecommended.setVisibility(View.GONE);
                    }
                });
    }

    // ── Sidebar Logic ────────────────────────────────────────────────────────

    private void closeOrFinish() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(android.view.Gravity.END)) {
            closeSidebar();
        } else {
            finish();
        }
    }

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

    private void setupSidebar() {
        findViewById(R.id.sidebar_btn_close).setOnClickListener(v -> closeSidebar());

        findViewById(R.id.sidebar_btn_home).setOnClickListener(v -> {
            closeSidebar();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.sidebar_btn_movies).setOnClickListener(v -> {
            closeSidebar();
            Intent intent = new Intent(this, MoviesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        
        findViewById(R.id.sidebar_btn_library).setOnClickListener(v -> {
            closeSidebar();
            Intent intent = new Intent(this, com.example.finalprojectandroiddev2.ui.library.LibraryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.sidebar_btn_about).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_about_coming_soon, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.sidebar_btn_team).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_team_coming_soon, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.sidebar_btn_signout).setOnClickListener(v -> logout());

        findViewById(R.id.sidebar_user_profile).setOnClickListener(v -> {
            closeSidebar();
            Toast.makeText(this, R.string.sidebar_nav_profile_coming_soon, Toast.LENGTH_SHORT).show();
        });

        loadUserProfile();
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
