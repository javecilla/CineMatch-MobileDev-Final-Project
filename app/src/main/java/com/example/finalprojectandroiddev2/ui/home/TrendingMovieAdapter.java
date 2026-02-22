package com.example.finalprojectandroiddev2.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.utils.Constants;

import java.util.List;

/**
 * RecyclerView adapter for the horizontal trending movie carousel on the Home screen.
 * Each card shows a movie poster with two overlay icon buttons:
 *  - Favourite (heart_outline_icon)
 *  - Watchlist (add_shadow_outline)
 */
public class TrendingMovieAdapter extends RecyclerView.Adapter<TrendingMovieAdapter.MovieViewHolder> {

    private final List<Movie> movies;

    public TrendingMovieAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_trending, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // Load poster with Glide
        String posterUrl = Constants.TMDB_IMAGE_BASE_URL + movie.getPosterPath();
        Glide.with(holder.ivPoster.getContext())
                .load(posterUrl)
                .centerCrop()
                .placeholder(R.color.color_surface)
                .error(R.color.color_surface)
                .into(holder.ivPoster);

        // Overlay button click listeners
        // holder.btnFavorite.setOnClickListener(v ->
        //         Toast.makeText(v.getContext(),
        //                 "\"" + movie.getTitle() + "\" added to Favorites",
        //                 Toast.LENGTH_SHORT).show());

        // holder.btnWatchlist.setOnClickListener(v ->
        //         Toast.makeText(v.getContext(),
        //                 "\"" + movie.getTitle() + "\" added to Watchlist",
        //                 Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    /** Update the dataset and refresh the list. */
    public void setMovies(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivPoster;
        // final ImageButton btnFavorite;
        // final ImageButton btnWatchlist;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster    = itemView.findViewById(R.id.iv_movie_poster);
            // btnFavorite = itemView.findViewById(R.id.btn_favorite);
            // btnWatchlist = itemView.findViewById(R.id.btn_watchlist);
        }
    }
}
