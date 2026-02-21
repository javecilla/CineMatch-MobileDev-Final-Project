package com.example.finalprojectandroiddev2.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Maps the top-level JSON response from TMDB list endpoints:
 *   /trending/movie/{time_window}
 *   /movie/popular
 *   /movie/top_rated
 *
 * Example:
 * {
 *   "page": 1,
 *   "results": [ {...}, {...} ],
 *   "total_pages": 500,
 *   "total_results": 10000
 * }
 */
public class MovieListResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<Movie> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public int getPage() { return page; }
    public List<Movie> getResults() { return results; }
    public int getTotalPages() { return totalPages; }
    public int getTotalResults() { return totalResults; }
}
