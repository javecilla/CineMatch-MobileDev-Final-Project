package com.example.finalprojectandroiddev2.ui.swiping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ViewPager2 adapter that displays full-screen swipeable movie cards.
 *
 * Each card has two overlay states:
 *   COLLAPSED (default) — shows movie title + release date over a gradient scrim
 *   EXPANDED  (on tap)  — additionally shows overview + genre chips
 *
 * Tapping the card toggles between the two states.
 */
public class MovieCardAdapter extends RecyclerView.Adapter<MovieCardAdapter.MovieCardViewHolder> {

    // ── TMDB Genre ID → Name (standard TMDB movie genre list) ─────────────────
    private static final Map<Integer, String> GENRE_MAP = new HashMap<>();
    static {
        GENRE_MAP.put(28,    "Action");
        GENRE_MAP.put(12,    "Adventure");
        GENRE_MAP.put(16,    "Animation");
        GENRE_MAP.put(35,    "Comedy");
        GENRE_MAP.put(80,    "Crime");
        GENRE_MAP.put(99,    "Documentary");
        GENRE_MAP.put(18,    "Drama");
        GENRE_MAP.put(10751, "Family");
        GENRE_MAP.put(14,    "Fantasy");
        GENRE_MAP.put(36,    "History");
        GENRE_MAP.put(27,    "Horror");
        GENRE_MAP.put(10402, "Music");
        GENRE_MAP.put(9648,  "Mystery");
        GENRE_MAP.put(10749, "Romance");
        GENRE_MAP.put(878,   "Sci-Fi");
        GENRE_MAP.put(10770, "TV Movie");
        GENRE_MAP.put(53,    "Thriller");
        GENRE_MAP.put(10752, "War");
        GENRE_MAP.put(37,    "Western");
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    private final List<Movie> movies = new ArrayList<>();

    /** Replace the current list and redraw all cards. */
    public void setMovies(List<Movie> newMovies) {
        movies.clear();
        if (newMovies != null) movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    // ── Adapter ───────────────────────────────────────────────────────────────

    @NonNull
    @Override
    public MovieCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_card, parent, false);
        return new MovieCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCardViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    static class MovieCardViewHolder extends RecyclerView.ViewHolder {

        private final ImageView  ivPoster;
        private final TextView   tvTitle;
        private final TextView   tvReleaseDate;
        private final TextView   tvRating;
        // Expanded-only views
        private final TextView   tvOverview;
        private final ChipGroup  chipGroupGenres;

        /** Tracks whether the card is in its expanded state. Starts true — expanded by default. */
        private boolean isExpanded = true;

        MovieCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster        = itemView.findViewById(R.id.image_poster);
            tvTitle         = itemView.findViewById(R.id.text_movie_title);
            tvReleaseDate   = itemView.findViewById(R.id.text_movie_release_date);
            tvRating        = itemView.findViewById(R.id.text_movie_rating);
            tvOverview      = itemView.findViewById(R.id.text_movie_overview);
            chipGroupGenres = itemView.findViewById(R.id.chip_group_card_genres);
        }

        void bind(Movie movie) {
            // Default to expanded state whenever card is (re)bound
            isExpanded = true;
            setExpandedState(true);

            // Title
            tvTitle.setText(movie.getTitle() != null ? movie.getTitle() : "");

            // Rating — top-right badge
            tvRating.setText(String.format(Locale.getDefault(), "⭐ %.1f", movie.getVoteAverage()));

            // Release date
            tvReleaseDate.setText(formatReleaseDate(movie.getReleaseDate()));

            // Overview
            tvOverview.setText(movie.getOverview() != null ? movie.getOverview() : "");

            // Genre chips
            buildGenreChips(itemView.getContext(), movie);

            // Image — prefer backdrop (wider aspect ratio), fall back to poster
            String imagePath;
            if (movie.getBackdropPath() != null && !movie.getBackdropPath().isEmpty()) {
                imagePath = Constants.TMDB_IMAGE_BASE_URL + movie.getBackdropPath();
            } else if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
                imagePath = Constants.TMDB_IMAGE_BASE_URL + movie.getPosterPath();
            } else {
                imagePath = null;
            }

            Glide.with(itemView.getContext())
                    .load(imagePath)
                    .centerCrop()
                    .placeholder(R.color.color_surface)
                    .error(R.color.color_surface)
                    .into(ivPoster);

            // Toggle expanded / collapsed on card tap
            itemView.setOnClickListener(v -> {
                isExpanded = !isExpanded;
                setExpandedState(isExpanded);
            });
        }

        /** Show or hide the expanded-only views (overview + genres). */
        private void setExpandedState(boolean expanded) {
            int visibility = expanded ? View.VISIBLE : View.GONE;
            tvOverview.setVisibility(visibility);
            chipGroupGenres.setVisibility(visibility);
        }

        /** Populates the ChipGroup with genre names resolved from genreIds. */
        private void buildGenreChips(Context ctx, Movie movie) {
            chipGroupGenres.removeAllViews();
            if (movie.getGenreIds() == null) return;

            for (int id : movie.getGenreIds()) {
                String name = GENRE_MAP.get(id);
                if (name == null) continue;

                Chip chip = new Chip(ctx);
                chip.setText(name);
                chip.setClickable(false);
                chip.setCheckable(false);
                chip.setChipBackgroundColorResource(R.color.color_surface);
                chip.setTextColor(ctx.getColor(R.color.color_text_primary));
                chip.setTextSize(11f);
                chipGroupGenres.addView(chip);
            }
        }

        /**
         * Converts raw TMDB date "YYYY-MM-DD" to a friendly "MMM yyyy" string.
         * e.g. "2026-02-25" → "Feb 2026". Returns "—" if null, empty, or unparseable.
         */
        private String formatReleaseDate(String rawDate) {
            if (rawDate == null || rawDate.isEmpty()) return "—";
            try {
                java.text.SimpleDateFormat inputFmt  = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                java.text.SimpleDateFormat outputFmt = new java.text.SimpleDateFormat("MMM yyyy",   Locale.getDefault());
                java.util.Date date = inputFmt.parse(rawDate);
                return date != null ? outputFmt.format(date) : rawDate;
            } catch (java.text.ParseException e) {
                return rawDate; // fallback to raw string
            }
        }

    }
}
