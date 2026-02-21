package com.example.finalprojectandroiddev2.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a single movie result from TMDB API.
 * Maps to the objects inside the "results" array of /movie/trending and /movie/popular.
 */
public class Movie {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("overview")
    private String overview;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

    // ── Getters ───────────────────────────────────────────────────────────────

    public int getId() { return id; }

    public String getTitle() { return title; }

    /** Raw poster path, e.g. "/abc123.jpg". Build full URL via TmdbImageUtil. */
    public String getPosterPath() { return posterPath; }

    public String getBackdropPath() { return backdropPath; }

    public String getOverview() { return overview; }

    public double getVoteAverage() { return voteAverage; }

    public String getReleaseDate() { return releaseDate; }
}
