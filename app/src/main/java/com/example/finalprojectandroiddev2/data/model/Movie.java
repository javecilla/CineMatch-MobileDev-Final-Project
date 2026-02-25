package com.example.finalprojectandroiddev2.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

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

    @SerializedName("genre_ids")
    private List<Integer> genreIds;

    /** Full genre objects — only present in /movie/{id} detail responses. */
    @SerializedName("genres")
    private List<Genre> genres;

    @SerializedName("popularity")
    private double popularity;

    // ── Getters ───────────────────────────────────────────────────────────────

    public int getId() { return id; }

    public String getTitle() { return title; }

    /** Raw poster path, e.g. "/abc123.jpg". Build full URL via TmdbImageUtil. */
    public String getPosterPath() { return posterPath; }

    public String getBackdropPath() { return backdropPath; }

    public String getOverview() { return overview; }

    public double getVoteAverage() { return voteAverage; }

    public String getReleaseDate() { return releaseDate; }

    public List<Integer> getGenreIds() { return genreIds; }

    public List<Genre> getGenres() { return genres; }

    public double getPopularity() { return popularity; }

    // ── Setters (used when rebuilding Movie from Firebase) ────────────────────

    public void setId(int id)                        { this.id           = id;          }
    public void setTitle(String title)               { this.title        = title;       }
    public void setPosterPath(String posterPath)     { this.posterPath   = posterPath;  }
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath;}
    public void setOverview(String overview)         { this.overview     = overview;    }
    public void setVoteAverage(double voteAverage)   { this.voteAverage  = voteAverage; }
    public void setReleaseDate(String releaseDate)   { this.releaseDate  = releaseDate; }
    public void setGenreIds(List<Integer> genreIds)  { this.genreIds     = genreIds;    }
    public void setGenres(List<Genre> genres)          { this.genres       = genres;      }

    // ── Genre inner class (detail endpoint only) ──────────────────────────────

    public static class Genre {
        @SerializedName("id")   private int    id;
        @SerializedName("name") private String name;
        public int    getId()   { return id;   }
        public String getName() { return name; }
    }
}

