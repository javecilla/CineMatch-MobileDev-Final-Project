package com.example.finalprojectandroiddev2.ui.home;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adapter for the "Popular Movies" vertical list on the Home screen.
 * Each card shows: poster | title, popularity score, genre chips, release date.
 */
public class PopularMovieAdapter extends RecyclerView.Adapter<PopularMovieAdapter.PopularViewHolder> {

    private final List<Movie> movies;

    // ── TMDB Genre ID → Name map ──────────────────────────────────────────────
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
        GENRE_MAP.put(878,   "Science Fiction");
        GENRE_MAP.put(10770, "TV Movie");
        GENRE_MAP.put(53,    "Thriller");
        GENRE_MAP.put(10752, "War");
        GENRE_MAP.put(37,    "Western");
    }

    // ── Date formatting ───────────────────────────────────────────────────────
    private static final SimpleDateFormat INPUT_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat OUTPUT_DATE_FORMAT =
            new SimpleDateFormat("MMM yyyy", Locale.getDefault());

    public PopularMovieAdapter(List<Movie> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
    }

    // ── Dataset update ────────────────────────────────────────────────────────

    public void setMovies(List<Movie> newMovies) {
        movies.clear();
        if (newMovies != null) movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    // ── RecyclerView.Adapter overrides ────────────────────────────────────────

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_popular, parent, false);
        return new PopularViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // Poster
        String posterUrl = Constants.TMDB_IMAGE_BASE_URL + movie.getPosterPath();
        Glide.with(holder.ivPoster.getContext())
                .load(posterUrl)
                .centerCrop()
                .placeholder(R.color.color_surface)
                .error(R.color.color_surface)
                .into(holder.ivPoster);

        // Title
        holder.tvTitle.setText(movie.getTitle());

        // Popularity — 1 decimal place
        holder.tvPopularity.setText(
                String.format(Locale.getDefault(), "%.1f", movie.getPopularity()));

        // Genre chips
        holder.chipGroupGenres.removeAllViews();
        List<Integer> genreIds = movie.getGenreIds();
        if (genreIds != null) {
            for (Integer id : genreIds) {
                String name = GENRE_MAP.get(id);
                if (name != null) {
                    Chip chip = new Chip(holder.chipGroupGenres.getContext());
                    chip.setText(name);
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    chip.setCloseIconVisible(false);
                    chip.setChipStrokeWidth(0f);
                    chip.setTextColor(ColorStateList.valueOf(
                            ContextCompat.getColor(
                                    holder.chipGroupGenres.getContext(),
                                    R.color.color_text_primary)));
                    chip.setChipBackgroundColor(ColorStateList.valueOf(
                            ContextCompat.getColor(
                                    holder.chipGroupGenres.getContext(),
                                    R.color.color_surface)));
                    chip.setTextSize(11f);
                    holder.chipGroupGenres.addView(chip);
                }
            }
        }

        // Release date: "YYYY-MM-DD"  →  "MMM yyyy"
        holder.tvReleaseDate.setText(formatReleaseDate(movie.getReleaseDate()));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    // ── Helper: date formatting ───────────────────────────────────────────────

    /**
     * Parses an ISO date string ("YYYY-MM-DD") and returns a human-friendly
     * "MMM yyyy" string (e.g. "Feb 2026").  Returns the original string on error.
     */
    private String formatReleaseDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) return "";
        try {
            Date date = INPUT_DATE_FORMAT.parse(rawDate);
            return date != null ? OUTPUT_DATE_FORMAT.format(date) : rawDate;
        } catch (ParseException e) {
            return rawDate;
        }
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    static class PopularViewHolder extends RecyclerView.ViewHolder {
        final ImageView  ivPoster;
        final TextView   tvTitle;
        final TextView   tvPopularity;
        final ChipGroup  chipGroupGenres;
        final TextView   tvReleaseDate;

        PopularViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster        = itemView.findViewById(R.id.iv_popular_poster);
            tvTitle         = itemView.findViewById(R.id.tv_popular_title);
            tvPopularity    = itemView.findViewById(R.id.tv_popular_popularity);
            chipGroupGenres = itemView.findViewById(R.id.chip_group_genres);
            tvReleaseDate   = itemView.findViewById(R.id.tv_popular_release_date);
        }
    }
}
