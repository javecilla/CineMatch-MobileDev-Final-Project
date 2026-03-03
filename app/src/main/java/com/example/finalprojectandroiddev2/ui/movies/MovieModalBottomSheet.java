package com.example.finalprojectandroiddev2.ui.movies;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.finalprojectandroiddev2.BuildConfig;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.api.TmdbApiClient;
import com.example.finalprojectandroiddev2.data.api.TmdbApiService;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.data.repository.FirebaseRepository;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieModalBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "MovieModalBottomSheet";
    private static final String ARG_MOVIE_ID = "arg_movie_id";

    private int movieId;
    private Movie currentMovie;

    // UI
    private ImageView ivPoster;
    private TextView tvTitle;
    private TextView tvPopularity;
    private TextView tvReleaseDate;
    private View rowPopularity;
    private View rowReleaseDate;
    private ChipGroup chipGroupGenres;
    
    private MaterialButton btnViewDetails;
    private MaterialButton btnWatchlist;
    private MaterialButton btnFavorites;

    private boolean isWatchlist = false;
    private boolean isFavorite = false;
    private String currentUserUid;

    // Dates
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("MMM yyyy", Locale.getDefault());

    public static MovieModalBottomSheet newInstance(int movieId) {
        MovieModalBottomSheet fragment = new MovieModalBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt(ARG_MOVIE_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        
        // Prevent dismissal by clicking outside or swiping down
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        
        // Set state to expanded
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_movie_modal, container, false);

        // Bind UI Components
        ivPoster = view.findViewById(R.id.iv_modal_poster);
        tvTitle = view.findViewById(R.id.tv_modal_title);
        tvPopularity = view.findViewById(R.id.tv_modal_popularity);
        tvReleaseDate = view.findViewById(R.id.tv_modal_release_date);
        rowPopularity = view.findViewById(R.id.row_modal_popularity);
        rowReleaseDate = view.findViewById(R.id.row_modal_release_date);
        chipGroupGenres = view.findViewById(R.id.chip_group_modal_genres);
        
        btnViewDetails = view.findViewById(R.id.btn_modal_full_details);
        btnWatchlist = view.findViewById(R.id.btn_modal_watchlist);
        btnFavorites = view.findViewById(R.id.btn_modal_favorite);

        ImageView btnClose = view.findViewById(R.id.btn_close_modal);

        // Hide UI until loaded
        setUiVisibility(View.INVISIBLE);

        // Close explicitly via button
        btnClose.setOnClickListener(v -> dismiss());

        // Fetch Data
        fetchMovieDetails();

        return view;
    }

    private void setUiVisibility(int visibility) {
        if (ivPoster != null) ivPoster.setVisibility(visibility);
        if (tvTitle != null) tvTitle.setVisibility(visibility);
        
        if (rowPopularity != null) rowPopularity.setVisibility(visibility);
        if (rowReleaseDate != null) rowReleaseDate.setVisibility(visibility);
        
        if (chipGroupGenres != null) chipGroupGenres.setVisibility(visibility);
        if (btnViewDetails != null) btnViewDetails.setVisibility(visibility);
        if (btnWatchlist != null) btnWatchlist.setVisibility(visibility);
        if (btnFavorites != null) btnFavorites.setVisibility(visibility);
    }

    private void fetchMovieDetails() {
        TmdbApiService apiService = TmdbApiClient.getService();
        String token = "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN;

        apiService.getMovieDetails(movieId, "en-US", token).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentMovie = response.body();
                    populateUi(currentMovie);
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load movie details", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });
    }

    private void populateUi(Movie movie) {
        if (getContext() == null) return;
        
        // Poster
        String posterUrl = Constants.TMDB_IMAGE_BASE_URL + movie.getPosterPath();
        Glide.with(this)
                .load(posterUrl)
                .centerCrop()
                .placeholder(R.color.color_surface)
                .error(R.color.color_surface)
                .into(ivPoster);

        // Text views
        tvTitle.setText(movie.getTitle());
        tvPopularity.setText(String.format(Locale.getDefault(), "%.1f", movie.getPopularity()));
        tvReleaseDate.setText(formatReleaseDate(movie.getReleaseDate()));

        // Genre chips
        chipGroupGenres.removeAllViews();
        List<Movie.Genre> genres = movie.getGenres();
        if (genres != null) {
            for (Movie.Genre genre : genres) {
                Chip chip = new Chip(getContext());
                chip.setText(genre.getName());
                chip.setClickable(false);
                chip.setCheckable(false);
                chip.setCloseIconVisible(false);
                chip.setChipStrokeWidth(0f);
                chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_text_primary)));
                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_surface)));
                chip.setTextSize(11f);
                chipGroupGenres.addView(chip);
            }
        }

        // Show UI
        setUiVisibility(View.VISIBLE);
        
        // Setup Action Listeners
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserUid = user.getUid();
            checkInitialLibraryStates();
        } else {
            setupActionButtons(); // Setup with default listeners
        }
    }
    
    private void checkInitialLibraryStates() {
        FirebaseRepository repo = FirebaseRepository.getInstance();
        
        // Check Favorites
        repo.checkIfFavorite(currentUserUid, movieId, exists -> {
            isFavorite = exists;
            updateFavoriteButtonIcon();
        });

        // Check Watchlist
        repo.checkIfWatchlist(currentUserUid, movieId, exists -> {
            isWatchlist = exists;
            updateWatchlistButtonIcon();
        });

        // Add Listeners
        setupActionButtons();
    }

    private void updateFavoriteButtonIcon() {
        if (btnFavorites != null && getContext() != null) {
            int iconRes = isFavorite ? R.drawable.heart_solid_icon : R.drawable.heart_outline_icon;
            btnFavorites.setIcon(ContextCompat.getDrawable(getContext(), iconRes));
            btnFavorites.setText(isFavorite ? "Remove from Favorites" : "Add to Favorites");
        }
    }

    private void updateWatchlistButtonIcon() {
        if (btnWatchlist != null && getContext() != null) {
            int iconRes = isWatchlist ? R.drawable.add_item_fill : R.drawable.add_item_outline;
            btnWatchlist.setIcon(ContextCompat.getDrawable(getContext(), iconRes));
            btnWatchlist.setText(isWatchlist ? "Remove from Watchlist" : "Add to Watchlist");
        }
    }
    
    private void setupActionButtons() {
        btnViewDetails.setOnClickListener(v -> {
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), ViewMovieActivity.class);
                intent.putExtra(ViewMovieActivity.EXTRA_MOVIE_ID, movieId);
                startActivity(intent);
                dismiss();
            }
        });
        
        btnWatchlist.setOnClickListener(v -> {
            if (currentUserUid == null || currentMovie == null) {
                Toast.makeText(getContext(), "Must be logged in", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseRepository repo = FirebaseRepository.getInstance();
            btnWatchlist.setEnabled(false); // prevent double clicks

            if (isWatchlist) {
                repo.removeFromWatchlist(currentUserUid, movieId, new FirebaseRepository.SimpleCallback() {
                    @Override public void onSuccess() {
                        isWatchlist = false;
                        updateWatchlistButtonIcon();
                        btnWatchlist.setEnabled(true);
                        Toast.makeText(getContext(), "Removed from Watchlist", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onFailure(String message) {
                        btnWatchlist.setEnabled(true);
                        Toast.makeText(getContext(), "Failed: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                repo.addToWatchlist(currentUserUid, currentMovie, new FirebaseRepository.SimpleCallback() {
                    @Override public void onSuccess() {
                        isWatchlist = true;
                        updateWatchlistButtonIcon();
                        btnWatchlist.setEnabled(true);
                        Toast.makeText(getContext(), "Added to Watchlist", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onFailure(String message) {
                        btnWatchlist.setEnabled(true);
                        Toast.makeText(getContext(), "Failed: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        
        btnFavorites.setOnClickListener(v -> {
            if (currentUserUid == null || currentMovie == null) {
                Toast.makeText(getContext(), "Must be logged in", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseRepository repo = FirebaseRepository.getInstance();
            btnFavorites.setEnabled(false); // prevent double clicks

            if (isFavorite) {
                repo.removeFromFavorites(currentUserUid, movieId, new FirebaseRepository.SimpleCallback() {
                    @Override public void onSuccess() {
                        isFavorite = false;
                        updateFavoriteButtonIcon();
                        btnFavorites.setEnabled(true);
                        Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onFailure(String message) {
                        btnFavorites.setEnabled(true);
                        Toast.makeText(getContext(), "Failed: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                repo.addToFavorites(currentUserUid, currentMovie, new FirebaseRepository.SimpleCallback() {
                    @Override public void onSuccess() {
                        isFavorite = true;
                        updateFavoriteButtonIcon();
                        btnFavorites.setEnabled(true);
                        Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onFailure(String message) {
                        btnFavorites.setEnabled(true);
                        Toast.makeText(getContext(), "Failed: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String formatReleaseDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) return "";
        try {
            Date date = INPUT_DATE_FORMAT.parse(rawDate);
            return date != null ? OUTPUT_DATE_FORMAT.format(date) : rawDate;
        } catch (ParseException e) {
            return rawDate;
        }
    }
}
