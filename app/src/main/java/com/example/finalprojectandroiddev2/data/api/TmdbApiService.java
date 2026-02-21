package com.example.finalprojectandroiddev2.data.api;

import com.example.finalprojectandroiddev2.data.model.MovieListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface for TMDB API endpoints.
 * Bearer token is passed per-call via the Authorization header.
 */
public interface TmdbApiService {

    /**
     * GET /trending/movie/{time_window}
     * Returns a list of trending movies for the day or week.
     *
     * @param timeWindow  "day" or "week"
     * @param language    e.g. "en-US"
     * @param bearerToken "Bearer <READ_ACCESS_TOKEN>"
     */
    @GET("trending/movie/{time_window}")
    Call<MovieListResponse> getTrendingMovies(
            @Path("time_window") String timeWindow,
            @Query("language") String language,
            @Header("Authorization") String bearerToken
    );

    /**
     * GET /movie/popular
     */
    @GET("movie/popular")
    Call<MovieListResponse> getPopularMovies(
            @Query("language") String language,
            @Query("page") int page,
            @Header("Authorization") String bearerToken
    );
}
