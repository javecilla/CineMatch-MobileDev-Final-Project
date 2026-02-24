package com.example.finalprojectandroiddev2.utils;

import com.google.firebase.database.DataSnapshot;

/**
 * Utility class encapsulating the match-detection condition.
 *
 * <p>A match occurs when <em>every</em> current lobby member has voted Yes on the same movie.
 * Because members can leave mid-session (their entry is removed from {@code members/}),
 * this class always works against the <strong>current live member count</strong> from a
 * fresh Firebase snapshot — a departing member's vote requirement naturally drops.</p>
 *
 * <p>All methods are {@code static} — this class is never instantiated.</p>
 */
public final class MatchDetector {

    private MatchDetector() {}

    /**
     * Returns {@code true} when all current members have voted Yes on a movie.
     *
     * @param voteCount   number of Yes votes recorded for the movie
     * @param memberCount current number of members in the lobby
     * @return {@code true} if {@code memberCount > 0} and {@code voteCount >= memberCount}
     */
    public static boolean isMatch(long voteCount, long memberCount) {
        return memberCount > 0 && voteCount >= memberCount;
    }

    /**
     * Reads the live member count from a full lobby {@link DataSnapshot}.
     * Using a fresh snapshot guarantees the count reflects any members who may
     * have left since the session started.
     *
     * @param lobbySnapshot snapshot of {@code lobbies/{roomCode}}
     * @return current number of members (0 if the node is missing)
     */
    public static long memberCount(DataSnapshot lobbySnapshot) {
        DataSnapshot membersSnap = lobbySnapshot.child(Constants.NODE_MEMBERS);
        return membersSnap.getChildrenCount();
    }

    /**
     * Reads the Yes-vote count for a specific movie from a full lobby snapshot.
     *
     * @param lobbySnapshot snapshot of {@code lobbies/{roomCode}}
     * @param movieId       TMDB movie ID to check
     * @return number of Yes votes for the given movie (0 if the node is missing)
     */
    public static long voteCount(DataSnapshot lobbySnapshot, int movieId) {
        return lobbySnapshot
                .child(Constants.NODE_VOTES)
                .child(String.valueOf(movieId))
                .getChildrenCount();
    }
}
