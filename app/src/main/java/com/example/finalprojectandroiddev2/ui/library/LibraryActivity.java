package com.example.finalprojectandroiddev2.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.data.repository.AuthRepository;
import com.example.finalprojectandroiddev2.data.repository.FirebaseRepository;
import com.example.finalprojectandroiddev2.data.repository.UserRepository;
import com.example.finalprojectandroiddev2.model.UserProfile;
import com.example.finalprojectandroiddev2.ui.auth.LoginActivity;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.movies.MovieModalBottomSheet;
import com.example.finalprojectandroiddev2.ui.movies.MoviesActivity;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView rvLibraryMovies;
    private LibraryMovieAdapter adapter;
    private ProgressBar pbLoading;
    private TextView tvInfoMessage;

    private LinearLayout tabWatchlist;
    private LinearLayout tabFavorites;
    private TextView tvTabWatchlist;
    private TextView tvTabFavorites;
    private View indicatorWatchlist;
    private View indicatorFavorites;

    private boolean isWatchlistActive = true; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        applyEdgeToEdgeInsets(R.id.container_library);

        drawerLayout = findViewById(R.id.drawer_layout);
        rvLibraryMovies = findViewById(R.id.rv_library_movies);
        pbLoading = findViewById(R.id.pb_library_loading);
        tvInfoMessage = findViewById(R.id.tv_library_info_message);

        tabWatchlist = findViewById(R.id.tab_watchlist);
        tabFavorites = findViewById(R.id.tab_favorites);
        tvTabWatchlist = findViewById(R.id.tv_tab_watchlist);
        tvTabFavorites = findViewById(R.id.tv_tab_favorites);
        indicatorWatchlist = findViewById(R.id.indicator_watchlist);
        indicatorFavorites = findViewById(R.id.indicator_favorites);

        // Sidebar actions
        findViewById(R.id.btn_menu).setOnClickListener(v -> openSidebar());
        findViewById(R.id.sidebar_btn_close).setOnClickListener(v -> closeSidebar());
        
        // Highlight Library in sidebar
        TextView btnHome = findViewById(R.id.sidebar_btn_home);
        if (btnHome != null) {
            btnHome.setTextColor(ContextCompat.getColor(this, R.color.color_text_secondary));
        }
        TextView btnLibrary = findViewById(R.id.sidebar_btn_library);
        if (btnLibrary != null) {
            btnLibrary.setTextColor(ContextCompat.getColor(this, R.color.color_primary));
        }

        findViewById(R.id.sidebar_btn_home).setOnClickListener(v -> {
            closeSidebar();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.sidebar_btn_library).setOnClickListener(v -> closeSidebar());

        findViewById(R.id.sidebar_btn_movies).setOnClickListener(v -> {
            closeSidebar();
            startActivity(new Intent(this, MoviesActivity.class));
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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(android.view.Gravity.END)) {
                    closeSidebar();
                } else {
                    finish();
                }
            }
        });

        // Tabs
        tabWatchlist.setOnClickListener(v -> switchTab(true));
        tabFavorites.setOnClickListener(v -> switchTab(false));

        // RecyclerView Setup
        rvLibraryMovies.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LibraryMovieAdapter(new ArrayList<>(), movie -> {
            MovieModalBottomSheet modal = MovieModalBottomSheet.newInstance(movie.getId());
            modal.show(getSupportFragmentManager(), MovieModalBottomSheet.TAG);
        });
        rvLibraryMovies.setAdapter(adapter);

        // Sidebar user profile init
        loadUserProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Always refresh the data when returning (since modal could have updated favorites/watchlist)
        switchTab(isWatchlistActive); 
    }

    private void switchTab(boolean toWatchlist) {
        isWatchlistActive = toWatchlist;

        // UI Updates for Tabs
        if (isWatchlistActive) {
            tvTabWatchlist.setTextColor(ContextCompat.getColor(this, R.color.color_primary));
            tvTabWatchlist.setTypeface(null, android.graphics.Typeface.BOLD);
            indicatorWatchlist.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary));

            tvTabFavorites.setTextColor(ContextCompat.getColor(this, R.color.color_text_secondary));
            tvTabFavorites.setTypeface(null, android.graphics.Typeface.NORMAL);
            indicatorFavorites.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            
            tvInfoMessage.setText(R.string.info_watchlist_showing);
        } else {
            tvTabFavorites.setTextColor(ContextCompat.getColor(this, R.color.color_primary));
            tvTabFavorites.setTypeface(null, android.graphics.Typeface.BOLD);
            indicatorFavorites.setBackgroundColor(ContextCompat.getColor(this, R.color.color_primary));

            tvTabWatchlist.setTextColor(ContextCompat.getColor(this, R.color.color_text_secondary));
            tvTabWatchlist.setTypeface(null, android.graphics.Typeface.NORMAL);
            indicatorWatchlist.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            
            tvInfoMessage.setText(R.string.info_favorites_showing);
        }

        fetchData();
    }

    private void fetchData() {
        FirebaseUser user = AuthRepository.getInstance().getCurrentUser();
        if (user == null) return;

        pbLoading.setVisibility(View.VISIBLE);
        rvLibraryMovies.setVisibility(View.GONE);

        String uid = user.getUid();

        FirebaseRepository.MovieQueueCallback callback = new FirebaseRepository.MovieQueueCallback() {
            @Override
            public void onLoaded(List<Movie> movies) {
                pbLoading.setVisibility(View.GONE);
                if (movies.isEmpty()) {
                    tvInfoMessage.setText(isWatchlistActive ? R.string.info_watchlist_empty : R.string.info_favorites_empty);
                } else {
                    tvInfoMessage.setText(isWatchlistActive ? R.string.info_watchlist_showing : R.string.info_favorites_showing);
                }
                adapter.setMovies(movies);
                rvLibraryMovies.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(LibraryActivity.this, "Failed to load: " + message, Toast.LENGTH_SHORT).show();
            }
        };

        if (isWatchlistActive) {
            FirebaseRepository.getInstance().getWatchlist(uid, callback);
        } else {
            FirebaseRepository.getInstance().getFavorites(uid, callback);
        }
    }

    // -- Sidebar & Profile --
    
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
