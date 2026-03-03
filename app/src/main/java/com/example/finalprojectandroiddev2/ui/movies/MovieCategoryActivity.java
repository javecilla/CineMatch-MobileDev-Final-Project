package com.example.finalprojectandroiddev2.ui.movies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroiddev2.BuildConfig;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.api.TmdbApiClient;
import com.example.finalprojectandroiddev2.data.api.TmdbApiService;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.data.model.MovieListResponse;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.PopularMovieAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieCategoryActivity extends BaseActivity {

    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String CATEGORY_TRENDING = "TRENDING";
    public static final String CATEGORY_POPULAR = "POPULAR";
    public static final String CATEGORY_TOP_RATED = "TOP_RATED";

    private String currentCategory = "";
    private int currentPage = 1;
    private int totalPages = 1;
    private int totalResults = 0;
    private boolean isLoading = false;

    private PopularMovieAdapter moviesAdapter;
    private RecyclerView rvCategoryMovies;
    private FloatingActionButton btnFabScrollTop;

    private TextView textCategoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_category);
        applyEdgeToEdgeInsets(R.id.container_category_movies);

        // UI references
        textCategoryTitle = findViewById(R.id.text_category_title);
        btnFabScrollTop = findViewById(R.id.btn_fab_scroll_top);

        // Toolbar
        findViewById(R.id.btn_back_category).setOnClickListener(v -> finish());
        
        // Disable menu
        View btnMenu = findViewById(R.id.btn_menu);
        if (btnMenu != null) {
            btnMenu.setVisibility(View.GONE);
        }

        // RecyclerView
        rvCategoryMovies = findViewById(R.id.rv_category_movies);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvCategoryMovies.setLayoutManager(layoutManager);
        moviesAdapter = new PopularMovieAdapter(new ArrayList<>(),
            null, // click listener
            movie -> { // long click listener
                com.example.finalprojectandroiddev2.ui.movies.MovieModalBottomSheet modal = 
                        com.example.finalprojectandroiddev2.ui.movies.MovieModalBottomSheet.newInstance(movie.getId());
                modal.show(getSupportFragmentManager(), com.example.finalprojectandroiddev2.ui.movies.MovieModalBottomSheet.TAG);
            }
        );
        rvCategoryMovies.setAdapter(moviesAdapter);

        // Infinite Scroll Listener
        rvCategoryMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Show FAB if scrolled down, hide if near top
                if (dy > 0 || layoutManager.findFirstVisibleItemPosition() > 5) {
                    btnFabScrollTop.show();
                } else {
                    btnFabScrollTop.hide();
                }

                // Load more logic
                if (!isLoading && currentPage < totalPages) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        isLoading = true;
                        currentPage++;
                        loadMovies(currentPage);
                    }
                }
            }
        });

        // FAB Click -> Scroll to top
        btnFabScrollTop.setOnClickListener(v -> {
            rvCategoryMovies.smoothScrollToPosition(0);
        });

        // Get category
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_CATEGORY)) {
            currentCategory = intent.getStringExtra(EXTRA_CATEGORY);
            
            // Set dynamic title based on the category
            switch (currentCategory) {
                case CATEGORY_TRENDING:
                    textCategoryTitle.setText("Trending Movies");
                    break;
                case CATEGORY_POPULAR:
                    textCategoryTitle.setText("Popular Movies");
                    break;
                case CATEGORY_TOP_RATED:
                    textCategoryTitle.setText("Top Rated Movies");
                    break;
                default:
                    textCategoryTitle.setText("Movies");
                    break;
            }

            if (currentCategory != null && !currentCategory.trim().isEmpty()) {
                loadMovies(1);
            } else {
                Toast.makeText(this, "Empty category", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadMovies(int page) {
        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiService api = TmdbApiClient.getService();
        Call<MovieListResponse> call = null;

        switch (currentCategory) {
            case CATEGORY_TRENDING:
                call = api.getTrendingMovies("day", "en-US", page, bearer);
                break;
            case CATEGORY_POPULAR:
                call = api.getPopularMovies("en-US", page, bearer);
                break;
            case CATEGORY_TOP_RATED:
                call = api.getTopRatedMovies("en-US", page, bearer);
                break;
        }

        if (call != null) {
            call.enqueue(new Callback<MovieListResponse>() {
                @Override
                public void onResponse(@NonNull Call<MovieListResponse> call, @NonNull Response<MovieListResponse> response) {
                    isLoading = false;
                    if (response.isSuccessful() && response.body() != null) {
                        MovieListResponse body = response.body();
                        
                        currentPage = body.getPage();
                        totalPages = body.getTotalPages();
                        totalResults = body.getTotalResults();

                        List<Movie> results = body.getResults();
                        if (results != null) {
                            if (currentPage == 1) {
                                moviesAdapter.setMovies(results);
                            } else {
                                moviesAdapter.addMovies(results);
                            }
                        }
                    } else {
                        Toast.makeText(MovieCategoryActivity.this, "Failed to load movies", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieListResponse> call, @NonNull Throwable t) {
                    isLoading = false;
                    Toast.makeText(MovieCategoryActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
