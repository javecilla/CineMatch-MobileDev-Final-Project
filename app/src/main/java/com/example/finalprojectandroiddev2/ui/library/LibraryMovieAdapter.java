package com.example.finalprojectandroiddev2.ui.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.model.Movie;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LibraryMovieAdapter extends RecyclerView.Adapter<LibraryMovieAdapter.LibraryViewHolder> {

    private List<Movie> movies;
    private final OnMovieLongClickListener longClickListener;

    public interface OnMovieLongClickListener {
        void onMovieLongClick(Movie movie);
    }

    public LibraryMovieAdapter(List<Movie> movies, OnMovieLongClickListener longClickListener) {
        this.movies = movies;
        this.longClickListener = longClickListener;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_library, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        Movie movie = movies.get(position);
        
        holder.tvTitle.setText(movie.getTitle() != null ? movie.getTitle() : "Unknown Title");
        
        String genres = movie.getGenreString();
        holder.tvGenres.setText(genres.isEmpty() ? "No Genres" : genres);

        // Backdrop or poster is wider, we prefer backdrop if available, otherwise poster
        String imagePath = movie.getBackdropPath() != null && !movie.getBackdropPath().isEmpty() 
            ? movie.getBackdropPath() : movie.getPosterPath();

        if (imagePath != null && !imagePath.isEmpty()) {
            String imageUrl = "https://image.tmdb.org/t/p/w780" + imagePath;
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.color.color_surface)
                    .error(R.color.color_surface)
                    .into(holder.ivPoster);
        } else {
            holder.ivPoster.setImageResource(R.color.color_surface);
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onMovieLongClick(movie);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    static class LibraryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle;
        TextView tvGenres;

        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_library_poster);
            tvTitle = itemView.findViewById(R.id.tv_library_title);
            tvGenres = itemView.findViewById(R.id.tv_library_genres);
        }
    }
}
