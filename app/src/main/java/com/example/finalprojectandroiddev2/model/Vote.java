package com.example.finalprojectandroiddev2.model;

/**
 * Represents a swiping vote: userId voted liked/disliked on movieId.
 * Maps to Firebase movies/{movieId}/votes/{userId} = true (liked) or false (disliked).
 */
public class Vote {

    private String userId;
    private int movieId;
    private boolean liked;

    public Vote() {
    }

    public Vote(String userId, int movieId, boolean liked) {
        this.userId = userId;
        this.movieId = movieId;
        this.liked = liked;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
