package com.example.finalprojectandroiddev2.ui.movies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchedMovieResultActivity extends BaseActivity {

    public static final String EXTRA_QUERY = "extra_query";

    private String currentQuery = "";
    private int currentPage = 1;
    private int totalPages = 1;
    private int totalResults = 0;

    private PopularMovieAdapter moviesAdapter;

    private TextView textSearchContext;
    private TextView textPaginationInfo;
    private MaterialButton btnPrevPage;
    private MaterialButton btnNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_movie_result);
        applyEdgeToEdgeInsets(R.id.container_searched_movies);

        // UI references
        textSearchContext = findViewById(R.id.text_search_context);
        textPaginationInfo = findViewById(R.id.text_pagination_info);
        btnPrevPage = findViewById(R.id.btn_page_prev);
        btnNextPage = findViewById(R.id.btn_page_next);

        // Toolbar
        findViewById(R.id.btn_back_search).setOnClickListener(v -> finish());
        
        // Disable menu since this isn't a top-level destination, or hide it
        View btnMenu = findViewById(R.id.btn_menu);
        if (btnMenu != null) {
            btnMenu.setVisibility(View.GONE);
        }

        // RecyclerView
        RecyclerView rvSearchedMovies = findViewById(R.id.rv_searched_movies);
        rvSearchedMovies.setLayoutManager(new LinearLayoutManager(this));
        moviesAdapter = new PopularMovieAdapter(new ArrayList<>());
        rvSearchedMovies.setAdapter(moviesAdapter);

        // Pagination handlers
        btnPrevPage.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                performSearch(currentPage);
            }
        });

        btnNextPage.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                performSearch(currentPage);
            }
        });

        // Initialize state
        updatePaginationButtons();

        // Get query
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_QUERY)) {
            currentQuery = intent.getStringExtra(EXTRA_QUERY);
            if (currentQuery != null && !currentQuery.trim().isEmpty()) {
                performSearch(1);
            } else {
                Toast.makeText(this, "Empty search query", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void performSearch(int page) {
        String bearer = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;
        TmdbApiService api = TmdbApiClient.getService();

        api.searchMovies(currentQuery, false, "en-US", page, bearer)
                .enqueue(new Callback<MovieListResponse>() {
                    @Override
                    public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MovieListResponse body = response.body();
                            
                            currentPage = body.getPage();
                            totalPages = body.getTotalPages();
                            totalResults = body.getTotalResults();

                            List<Movie> results = body.getResults();
                            if (results != null) {
                                moviesAdapter.setMovies(results);
                            } else {
                                moviesAdapter.setMovies(new ArrayList<>());
                            }

                            updateUI();
                        } else {
                            Toast.makeText(SearchedMovieResultActivity.this, "Search failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieListResponse> call, Throwable t) {
                        Toast.makeText(SearchedMovieResultActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI() {
        // Context Text: "X result(s) for query 'Y'"
        String contextText = getString(R.string.text_search_context, totalResults, currentQuery);
        textSearchContext.setText(contextText);

        // Pagination Info Text: "Showing X out of Y results"
        int maxShown = Math.min((currentPage * 20), totalResults); // TMDB pages are 20 items usually
        
        // Use the cumulative number of items up to the current page (maxShown)
        int displayCount = (totalResults == 0) ? 0 : maxShown;
        String paginationText = getString(R.string.text_pagination_info, displayCount, totalResults);
        textPaginationInfo.setText(paginationText);

        updatePaginationButtons();
    }

    private void updatePaginationButtons() {
        btnPrevPage.setEnabled(currentPage > 1);
        btnNextPage.setEnabled(currentPage < totalPages);
        
        // Optional: fade alpha when disabled for better UX
        btnPrevPage.setAlpha(currentPage > 1 ? 1.0f : 0.4f);
        btnNextPage.setAlpha(currentPage < totalPages ? 1.0f : 0.4f);
    }
}
