package com.example.finalprojectandroiddev2.ui.home;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for the "Top Rated Movies" horizontal carousel on the Home screen.
 * Displays a poster, title, and comma-separated genre names derived from TMDB genre IDs.
 */
public class TopRatedMovieAdapter extends RecyclerView.Adapter<TopRatedMovieAdapter.TopRatedViewHolder> {

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

    public TopRatedMovieAdapter(List<Movie> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
    }

    // ── Dataset update ────────────────────────────────────────────────────────

    /** Replace the current dataset and refresh the list. */
    public void setMovies(List<Movie> newMovies) {
        movies.clear();
        if (newMovies != null) movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    // ── RecyclerView.Adapter overrides ────────────────────────────────────────

    @NonNull
    @Override
    public TopRatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_top_rated, parent, false);
        return new TopRatedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopRatedViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // Load poster with Glide
        String posterUrl = Constants.TMDB_IMAGE_BASE_URL + movie.getPosterPath();
        Glide.with(holder.ivPoster.getContext())
                .load(posterUrl)
                .centerCrop()
                .placeholder(R.color.color_surface)
                .error(R.color.color_surface)
                .into(holder.ivPoster);

        // Title
        holder.tvTitle.setText(movie.getTitle());

        // Genres: convert IDs → names
        holder.tvGenres.setText(getGenresAsString(movie.getGenreIds()));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    // ── Genre helper ─────────────────────────────────────────────────────────

    /**
     * Converts a list of TMDB genre IDs into a comma-separated string of genre names.
     * Unknown IDs are silently ignored.
     *
     * @param genreIds List of integer genre IDs from the TMDB API response.
     * @return e.g. "Action, Adventure, Science Fiction"
     */
    private String getGenresAsString(List<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Integer id : genreIds) {
            String name = GENRE_MAP.get(id);
            if (name != null) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(name);
            }
        }
        return sb.toString();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    static class TopRatedViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivPoster;
        final TextView  tvTitle;
        final TextView  tvGenres;

        TopRatedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster  = itemView.findViewById(R.id.iv_top_rated_poster);
            tvTitle   = itemView.findViewById(R.id.tv_top_rated_title);
            tvGenres  = itemView.findViewById(R.id.tv_top_rated_genres);
        }
    }
}
