package com.example.finalprojectandroiddev2.data.api;

import com.example.finalprojectandroiddev2.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit client for TMDB.
 *
 * Usage:
 *   TmdbApiService service = TmdbApiClient.getService();
 *   service.getTrendingMovies("day", "en-US", "Bearer " + BuildConfig.TMDB_READ_ACCESS_TOKEN);
 */
public final class TmdbApiClient {

    private static TmdbApiService instance;

    private TmdbApiClient() {}

    public static TmdbApiService getService() {
        if (instance == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.TMDB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            instance = retrofit.create(TmdbApiService.class);
        }
        return instance;
    }
}
