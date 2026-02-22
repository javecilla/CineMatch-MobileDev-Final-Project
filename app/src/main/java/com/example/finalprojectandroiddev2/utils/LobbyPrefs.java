package com.example.finalprojectandroiddev2.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Thin helper for persisting the user's active lobby room code across back-presses
 * and app restarts.
 *
 * Written when a lobby is created or joined successfully.
 * Cleared only when the user explicitly taps "Leave Lobby".
 * HomeActivity reads this in onResume() and verifies membership with Firebase.
 */
public final class LobbyPrefs {

    private static final String PREF_FILE     = "lobby_prefs";
    private static final String KEY_ROOM_CODE = "active_room_code";

    private LobbyPrefs() {}

    /** Persist the room code the user is currently in. */
    public static void saveActiveRoomCode(Context ctx, String roomCode) {
        prefs(ctx).edit().putString(KEY_ROOM_CODE, roomCode).apply();
    }

    /**
     * Returns the stored room code, or {@code null} if the user is not in any lobby.
     */
    public static String getActiveRoomCode(Context ctx) {
        String code = prefs(ctx).getString(KEY_ROOM_CODE, null);
        return (code != null && !code.isEmpty()) ? code : null;
    }

    /** Call when the user explicitly leaves a lobby via "Leave Lobby" button. */
    public static void clearActiveRoomCode(Context ctx) {
        prefs(ctx).edit().remove(KEY_ROOM_CODE).apply();
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getApplicationContext()
                  .getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }
}
