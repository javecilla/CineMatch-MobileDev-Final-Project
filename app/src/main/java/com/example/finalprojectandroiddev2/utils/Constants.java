package com.example.finalprojectandroiddev2.utils;

/**
 * Application-wide constants. Secrets (API keys, tokens) must come from BuildConfig.
 */
public final class Constants {

    private Constants() {}

    // Log tags
    public static final String TAG_APP = "CineMatch";
    public static final String TAG_AUTH = "CineMatch.Auth";
    public static final String TAG_FIREBASE = "CineMatch.Firebase";
    public static final String TAG_TMDB = "CineMatch.TMDB";
    public static final String TAG_UI = "CineMatch.UI";

    // Firebase Realtime Database node names (lower_snake_case per APP_FLOW schema)
    public static final String NODE_LOBBIES = "lobbies";
    public static final String NODE_MEMBERS = "members";
    public static final String NODE_MOVIES = "movies";
    public static final String NODE_VOTES = "votes";
    public static final String NODE_MATCHED_MOVIE = "matchedMovie";

    // Lobby status values
    public static final String LOBBY_STATUS_WAITING = "waiting";
    public static final String LOBBY_STATUS_SWIPING = "swiping";
    public static final String LOBBY_STATUS_MATCHED = "matched";

    // TMDB API (base URL and paths; token from BuildConfig)
    public static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    public static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    // Swiping session
    public static final int SWIPING_SESSION_TIMEOUT_SECONDS = 120;

    // Room code
    public static final int ROOM_CODE_LENGTH = 6;
}
