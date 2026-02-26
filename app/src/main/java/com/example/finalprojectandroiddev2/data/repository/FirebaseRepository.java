package com.example.finalprojectandroiddev2.data.repository;

import androidx.annotation.NonNull;

import com.example.finalprojectandroiddev2.BuildConfig;
import com.example.finalprojectandroiddev2.data.model.LobbyMember;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.example.finalprojectandroiddev2.utils.MatchDetector;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton repository for all Firebase Realtime Database operations related to lobbies.
 *
 * Firebase schema (lobbies/):
 *   lobbies/{roomCode}/
 *     hostId:    String   (current host, changes on host transfer)
 *     createdBy: String   (original creator UID — never changes)
 *     createdAt: long
 *     status:    "waiting" | "swiping" | "matched"
 *     members/
 *       {userId}/
 *         username:  String
 *         gender:    String
 *         joinedAt:  long
 *         host:      boolean  (key is 'host', not 'isHost', to match LobbyMember.isHost() getter)
 *     votes/
 *       {movieId}/          ← TMDB movie ID (as String)
 *         {userId}: true    ← present = this user voted Yes on this movie
 *     matchedMovieId: String  ← set when all members have voted Yes on the same movie
 */
public class FirebaseRepository {

    private static final String TAG = Constants.TAG_FIREBASE;

    // ── Singleton ──────────────────────────────────────────────────────────────

    private static FirebaseRepository instance;

    private final DatabaseReference lobbiesRef;

    /** Active listeners — kept so we can detach them on demand. */
    private ChildEventListener activeMembersListener;
    private ValueEventListener  activeStatusListener;
    private DatabaseReference   activeMembersRef;
    private DatabaseReference   activeStatusRef;
    private ChildEventListener  activeVotesListener;
    private DatabaseReference   activeVotesRef;

    private FirebaseRepository() {
        FirebaseDatabase db = FirebaseDatabase.getInstance(BuildConfig.FB_ROUTE_INSTANCE_URL);
        lobbiesRef = db.getReference(Constants.NODE_LOBBIES);
    }

    public static synchronized FirebaseRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseRepository();
        }
        return instance;
    }

    // ── Callbacks ──────────────────────────────────────────────────────────────

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public interface ExistsCallback {
        void onResult(boolean exists);
    }

    public interface MembersCallback {
        void onMemberAdded(String userId, LobbyMember member);
        void onMemberRemoved(String userId);
        void onMemberChanged(String userId, LobbyMember member);
    }

    public interface StatusCallback {
        void onStatusChanged(String status);
    }

    public interface MemberLoadCallback {
        /** Called with the member data, or {@code null} if the user is not in the lobby. */
        void onResult(LobbyMember member);
    }

    public interface MovieQueueCallback {
        /** Called with the full ordered list of movies loaded from Firebase. */
        void onLoaded(List<com.example.finalprojectandroiddev2.data.model.Movie> movies);
        void onError(String message);
    }

    public interface VoteCallback {
        /** Called when the vote was successfully written to Firebase. */
        void onVoteRecorded();
        /**
         * Called when all lobby members have voted Yes on the same movie.
         * @param movieId TMDB ID of the matched movie.
         */
        void onMatchFound(int movieId);
        void onError(String message);
    }

    public interface VotesCallback {
        /**
         * Called whenever any member casts or removes a vote for the current movie.
         * @param voterUserIds set of UIDs that have voted Yes on this movie so far.
         */
        void onVotesUpdated(java.util.Set<String> voterUserIds);
    }

    public interface AllMembersCallback {
        /** Called once with a map of userId → LobbyMember for all current members. */
        void onLoaded(Map<String, LobbyMember> members);
    }

    // ── Lobby Creation ──────────────────────────────────────────────────────────

    /**
     * Creates a lobby node and adds the host as the first member.
     *
     * lobbies/{roomCode}/ = { hostId, createdAt, status: "waiting" }
     * lobbies/{roomCode}/members/{hostId}/ = { username, gender, joinedAt, isHost: true }
     *
     * Uses two separate setValue() calls because Firebase does not allow '/' in
     * map keys when using setValue() on a flat HashMap.
     */
    public void createLobby(String roomCode, String hostId, String username, String gender,
                            SimpleCallback callback) {
        DatabaseReference lobbyRef = lobbiesRef.child(roomCode);

        // 1. Write lobby metadata (no nested paths in the map keys)
        Map<String, Object> lobbyData = new HashMap<>();
        lobbyData.put("hostId",    hostId);
        lobbyData.put("createdBy", hostId);  // permanent record of original creator (UID)
        lobbyData.put("createdAt", System.currentTimeMillis());
        lobbyData.put("status",    Constants.LOBBY_STATUS_WAITING);

        // 2. Write host member data via proper .child() chaining
        Map<String, Object> memberData = new HashMap<>();
        memberData.put("username", username);
        memberData.put("gender",   gender != null ? gender : "");
        memberData.put("joinedAt", System.currentTimeMillis());
        memberData.put("host",     true);   // key must match getter isHost() → property "host"

        lobbyRef.setValue(lobbyData)
                .addOnSuccessListener(unused -> {
                    // Write host member only after lobby node exists
                    lobbyRef.child(Constants.NODE_MEMBERS)
                            .child(hostId)
                            .setValue(memberData)
                            .addOnSuccessListener(u -> {
                                Logger.d(TAG, "Lobby created: " + roomCode);
                                callback.onSuccess();
                            })
                            .addOnFailureListener(e -> {
                                Logger.e(TAG, "createLobby member write failed", e);
                                callback.onFailure(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Logger.e(TAG, "createLobby failed", e);
                    callback.onFailure(e.getMessage());
                });
    }

    // ── Lobby Existence Check ───────────────────────────────────────────────────

    /**
     * Single read — checks whether lobbies/{roomCode} exists.
     */
    public void lobbyExists(String roomCode, ExistsCallback callback) {
        lobbiesRef.child(roomCode).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onResult(task.getResult().exists());
            } else {
                // On error, assume not found to let the caller decide
                callback.onResult(false);
            }
        });
    }

    // ── Join Lobby ──────────────────────────────────────────────────────────────

    /**
     * Validates the lobby is open and not full, then adds the user as a member.
     * Calls onFailure if the lobby doesn't exist, is full, or has already started.
     */
    public void joinLobby(String roomCode, String userId, String username, String gender,
                          SimpleCallback callback) {
        DatabaseReference lobbyRef = lobbiesRef.child(roomCode);

        lobbyRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || !task.getResult().exists()) {
                callback.onFailure("Lobby not found.");
                return;
            }

            DataSnapshot snapshot = task.getResult();

            // Status check — can't join if already swiping or matched
            String status = snapshot.child("status").getValue(String.class);
            if (!Constants.LOBBY_STATUS_WAITING.equals(status)) {
                callback.onFailure("Session already started.");
                return;
            }

            // Capacity check (max 10 members)
            long memberCount = snapshot.child(Constants.NODE_MEMBERS).getChildrenCount();
            if (memberCount >= 10) {
                callback.onFailure("Lobby is full.");
                return;
            }

            // Add member
            Map<String, Object> memberData = new HashMap<>();
            memberData.put("username",  username);
            memberData.put("gender",    gender != null ? gender : "");
            memberData.put("joinedAt",  System.currentTimeMillis());
            memberData.put("host",      false);  // key must match getter isHost() → property "host"

            lobbyRef.child(Constants.NODE_MEMBERS).child(userId)
                    .setValue(memberData)
                    .addOnSuccessListener(unused -> {
                        Logger.d(TAG, "Joined lobby: " + roomCode + " as " + username);
                        callback.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Logger.e(TAG, "joinLobby failed", e);
                        callback.onFailure(e.getMessage());
                    });
        });
    }

    // ── Real-time Listeners ─────────────────────────────────────────────────────

    /**
     * Attaches a ChildEventListener to lobbies/{roomCode}/members/.
     * Notifies the caller when members are added, removed, or changed.
     */
    public void listenMembers(String roomCode, MembersCallback callback) {
        detachMembersListener(); // ensure no duplicate listeners

        activeMembersRef = lobbiesRef.child(roomCode).child(Constants.NODE_MEMBERS);
        activeMembersListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snap, String prev) {
                LobbyMember m = snap.getValue(LobbyMember.class);
                if (m != null) callback.onMemberAdded(snap.getKey(), m);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snap, String prev) {
                LobbyMember m = snap.getValue(LobbyMember.class);
                if (m != null) callback.onMemberChanged(snap.getKey(), m);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snap) {
                callback.onMemberRemoved(snap.getKey());
            }

            @Override public void onChildMoved(@NonNull DataSnapshot s, String p) {}
            @Override public void onCancelled(@NonNull DatabaseError e) {
                Logger.e(TAG, "listenMembers cancelled: " + e.getMessage());
            }
        };
        activeMembersRef.addChildEventListener(activeMembersListener);
    }

    /**
     * Attaches a ValueEventListener to lobbies/{roomCode}/status.
     */
    public void listenLobbyStatus(String roomCode, StatusCallback callback) {
        detachStatusListener();

        activeStatusRef = lobbiesRef.child(roomCode).child("status");
        activeStatusListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                String status = snap.getValue(String.class);
                if (status != null) callback.onStatusChanged(status);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Logger.e(TAG, "listenLobbyStatus cancelled: " + e.getMessage());
            }
        };
        activeStatusRef.addValueEventListener(activeStatusListener);
    }

    // ── Status Update ───────────────────────────────────────────────────────────

    /** Sets lobbies/{roomCode}/status to the given value. */
    public void setLobbyStatus(String roomCode, String status) {
        lobbiesRef.child(roomCode).child("status").setValue(status);
    }

    // ── Leave / Remove Member ───────────────────────────────────────────────────

    /**
     * Removes a member from the lobby.
     * Edge cases handled:
     * - Last member leaves  → entire lobby node is deleted.
     * - Host leaves + others remain → host badge is transferred to the next member.
     */
    public void removeMember(String roomCode, String userId, SimpleCallback callback) {
        DatabaseReference lobbyRef    = lobbiesRef.child(roomCode);
        DatabaseReference membersRef  = lobbyRef.child(Constants.NODE_MEMBERS);

        membersRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || !task.getResult().exists()) {
                if (callback != null) callback.onSuccess();
                return;
            }

            DataSnapshot membersSnap = task.getResult();
            long count = membersSnap.getChildrenCount();

            if (count <= 1) {
                // Last member — delete entire lobby
                lobbyRef.removeValue()
                        .addOnSuccessListener(u -> { if (callback != null) callback.onSuccess(); })
                        .addOnFailureListener(e -> { if (callback != null) callback.onFailure(e.getMessage()); });
                return;
            }

            // Check if the leaving member is the host.
            // Key is "host" (not "isHost") — matches LobbyMember.isHost() getter serialization.
            DataSnapshot leavingSnap = membersSnap.child(userId);
            Boolean leavingIsHost = leavingSnap.child("host").getValue(Boolean.class);

            // Remove the member first
            membersRef.child(userId).removeValue()
                    .addOnSuccessListener(unused -> {
                        if (Boolean.TRUE.equals(leavingIsHost)) {
                            // Transfer host badge to the next remaining member
                            for (DataSnapshot child : membersSnap.getChildren()) {
                                String candidateId = child.getKey();
                                if (candidateId != null && !candidateId.equals(userId)) {
                                    // Promote this member to host
                                    child.getRef().child("host").setValue(true);
                                    // Update lobby-level hostId (current host)
                                    lobbyRef.child("hostId").setValue(candidateId);
                                    Logger.d(TAG, "Host transferred to: " + candidateId);
                                    break;
                                }
                            }
                        }
                        Logger.d(TAG, "Member removed: " + userId + " from " + roomCode);
                        if (callback != null) callback.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Logger.e(TAG, "removeMember failed", e);
                        if (callback != null) callback.onFailure(e.getMessage());
                    });
        });
    }

    // ── Single-read helpers ─────────────────────────────────────────────────────

    /**
     * One-shot read of lobbies/{roomCode}/members/{userId}.
     * Returns the LobbyMember if the user is still in the lobby, or null if not.
     * Used by HomeActivity to verify membership before showing the return banner.
     */
    public void getMember(String roomCode, String userId, MemberLoadCallback callback) {
        lobbiesRef.child(roomCode)
                  .child(Constants.NODE_MEMBERS)
                  .child(userId)
                  .get()
                  .addOnCompleteListener(task -> {
                      if (task.isSuccessful() && task.getResult().exists()) {
                          LobbyMember m = task.getResult().getValue(LobbyMember.class);
                          callback.onResult(m);
                      } else {
                          callback.onResult(null);
                      }
                  });
    }

    // ── Movie Queue ─────────────────────────────────────────────────────────────

    /**
     * Writes an ordered list of movies to lobbies/{roomCode}/movies/
     * as a numbered map so Firebase preserves order:
     *   movies/0/{ id, title, overview, poster_path, backdrop_path, vote_average, release_date, genre_ids }
     *   movies/1/{ ... }
     *
     * Called by the host in LobbyActivity before setting status to "swiping".
     */
    public void saveMovieQueue(String roomCode,
                               List<com.example.finalprojectandroiddev2.data.model.Movie> movies,
                               SimpleCallback callback) {
        List<Map<String, Object>> queue = new ArrayList<>();
        for (com.example.finalprojectandroiddev2.data.model.Movie m : movies) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id",            m.getId());
            entry.put("title",         m.getTitle() != null        ? m.getTitle()        : "");
            entry.put("overview",      m.getOverview() != null     ? m.getOverview()     : "");
            entry.put("poster_path",   m.getPosterPath() != null   ? m.getPosterPath()   : "");
            entry.put("backdrop_path", m.getBackdropPath() != null ? m.getBackdropPath() : "");
            entry.put("vote_average",  m.getVoteAverage());
            entry.put("release_date",  m.getReleaseDate() != null  ? m.getReleaseDate()  : "");
            entry.put("genre_ids",     m.getGenreIds() != null     ? m.getGenreIds()     : new ArrayList<>());
            queue.add(entry);
        }

        lobbiesRef.child(roomCode)
                  .child(Constants.NODE_MOVIES)
                  .setValue(queue)
                  .addOnSuccessListener(u -> {
                      Logger.d(TAG, "Movie queue saved: " + movies.size() + " movies");
                      callback.onSuccess();
                  })
                  .addOnFailureListener(e -> {
                      Logger.e(TAG, "saveMovieQueue failed", e);
                      callback.onFailure(e.getMessage());
                  });
    }

    /**
     * One-shot read of lobbies/{roomCode}/movies/.
     * Deserialises each child into a Movie object and returns the ordered list.
     * Called by every member (including host) in SwipingActivity.
     */
    public void listenMovieQueue(String roomCode, MovieQueueCallback callback) {
        lobbiesRef.child(roomCode)
                  .child(Constants.NODE_MOVIES)
                  .get()
                  .addOnCompleteListener(task -> {
                      if (!task.isSuccessful() || !task.getResult().exists()) {
                          callback.onError("Movie queue not found.");
                          return;
                      }
                      List<com.example.finalprojectandroiddev2.data.model.Movie> movies = new ArrayList<>();
                      for (DataSnapshot snap : task.getResult().getChildren()) {
                          com.example.finalprojectandroiddev2.data.model.Movie m =
                                  new com.example.finalprojectandroiddev2.data.model.Movie();
                          m.setId(snap.child("id").getValue(Long.class) != null
                                  ? snap.child("id").getValue(Long.class).intValue() : 0);
                          m.setTitle(snap.child("title").getValue(String.class));
                          m.setOverview(snap.child("overview").getValue(String.class));
                          m.setPosterPath(snap.child("poster_path").getValue(String.class));
                          m.setBackdropPath(snap.child("backdrop_path").getValue(String.class));
                          Double avg = snap.child("vote_average").getValue(Double.class);
                          m.setVoteAverage(avg != null ? avg : 0.0);
                          m.setReleaseDate(snap.child("release_date").getValue(String.class));
                          // genre_ids stored as list of Long in Firebase
                          List<Integer> genreIds = new ArrayList<>();
                          for (DataSnapshot gSnap : snap.child("genre_ids").getChildren()) {
                              Long gId = gSnap.getValue(Long.class);
                              if (gId != null) genreIds.add(gId.intValue());
                          }
                          m.setGenreIds(genreIds);
                          movies.add(m);
                      }
                      Logger.d(TAG, "Movie queue loaded: " + movies.size() + " movies");
                      callback.onLoaded(movies);
                  });
    }

    // ── Vote Recording ──────────────────────────────────────────────────────────

    /**
     * Records a "Yes" vote for the current user on the given movie.
     *
     * Path written: lobbies/{roomCode}/votes/{movieId}/{userId} = true
     *
     * After writing, reads all votes for this movie and compares to member count.
     * If every member has voted Yes → sets matchedMovieId on the lobby and fires
     * onMatchFound so all devices can navigate to MatchActivity.
     *
     * "No" votes are intentionally NOT written — a missing user entry means No.
     *
     * @param roomCode lobby room code
     * @param userId   current Firebase Auth UID
     * @param movieId  TMDB movie ID (int)
     * @param callback result callback
     */
    public void recordVote(String roomCode, String userId, int movieId, VoteCallback callback) {
        String movieKey = String.valueOf(movieId);
        DatabaseReference voteRef = lobbiesRef
                .child(roomCode)
                .child(Constants.NODE_VOTES)
                .child(movieKey)
                .child(userId);

        voteRef.setValue(true)
                .addOnSuccessListener(unused -> {
                    Logger.d(TAG, "Vote recorded: " + userId + " → movie " + movieKey);
                    callback.onVoteRecorded();
                    checkForMatch(roomCode, movieId, callback);
                })
                .addOnFailureListener(e -> {
                    Logger.e(TAG, "recordVote failed", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Checks whether all current lobby members have voted Yes on the given movie.
     * If yes, writes matchedMovieId to the lobby and fires onMatchFound.
     */
    private void checkForMatch(String roomCode, int movieId, VoteCallback callback) {
        DatabaseReference lobbyRef = lobbiesRef.child(roomCode);

        // Single lobby snapshot → read both members and votes atomically
        lobbyRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || !task.getResult().exists()) return;

            DataSnapshot lobbySnap = task.getResult();
            long memberCount = MatchDetector.memberCount(lobbySnap);
            long voteCount   = MatchDetector.voteCount(lobbySnap, movieId);

            Logger.d(TAG, "Match check — movie " + movieId
                    + ": " + voteCount + "/" + memberCount + " votes");

            if (MatchDetector.isMatch(voteCount, memberCount)) {
                // All current members voted Yes → match!
                lobbyRef.child(Constants.NODE_MATCHED_MOVIE_ID)
                        .setValue(String.valueOf(movieId));
                setLobbyStatus(roomCode, Constants.LOBBY_STATUS_MATCHED);
                Logger.d(TAG, "Match found! Movie: " + movieId);
                callback.onMatchFound(movieId);
            }
        });
    }

    /**
     * One-shot read of {@code lobbies/{roomCode}/matchedMovieId}.
     * Used by {@code MatchActivity} on start to retrieve the matched movie's TMDB ID.
     *
     * @param roomCode lobby identifier
     * @param callback receives the movie ID string, or {@code null} if not found
     */
    public void getMatchedMovieId(String roomCode, MatchedMovieCallback callback) {
        lobbiesRef.child(roomCode)
                .child(Constants.NODE_MATCHED_MOVIE_ID)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || !task.getResult().exists()) {
                        callback.onLoaded(null);
                        return;
                    }
                    callback.onLoaded(task.getResult().getValue(String.class));
                });
    }

    /** Callback for {@link #getMatchedMovieId}. */
    public interface MatchedMovieCallback {
        /** @param movieId TMDB movie ID string, or {@code null} if not found */
        void onLoaded(String movieId);
    }

    // ── Cleanup ─────────────────────────────────────────────────────────────────

    /**
     * Detaches ALL active Firebase listeners.
     * Call from SwipingActivity.onDestroy() — the only place that owns ALL listeners.
     */
    public void detachListeners() {
        detachMembersListener();
        detachStatusListener();
        detachVotesListener();
        detachPageListener();
    }

    /**
     * Detaches only swiping-owned listeners (votes + currentPage).
     *
     * IMPORTANT: Do NOT detach the lobby status listener here.
     * MatchActivity may attach a status listener while SwipingActivity is finishing,
     * and detaching status in SwipingActivity.onDestroy() would remove the MatchActivity
     * listener (shared singleton), causing members to get stuck on the match screen.
     */
    public void detachSwipingListeners() {
        detachVotesListener();
        detachPageListener();
    }

    /**
     * Detaches only lobby-specific listeners (members + status).
     * Call from LobbyActivity / CreateLobbyActivity.onDestroy() so that
     * page and votes listeners (owned by SwipingActivity) survive the transition.
     */
    public void detachLobbyListeners() {
        detachMembersListener();
        detachStatusListener();
    }

    private void detachMembersListener() {
        if (activeMembersRef != null && activeMembersListener != null) {
            activeMembersRef.removeEventListener(activeMembersListener);
            activeMembersListener = null;
            activeMembersRef      = null;
        }
    }

    private void detachStatusListener() {
        if (activeStatusRef != null && activeStatusListener != null) {
            activeStatusRef.removeEventListener(activeStatusListener);
            activeStatusListener = null;
            activeStatusRef      = null;
        }
    }

    private void detachVotesListener() {
        if (activeVotesRef != null && activeVotesListener != null) {
            activeVotesRef.removeEventListener(activeVotesListener);
            activeVotesListener = null;
            activeVotesRef      = null;
        }
    }

    // ── Vote Sync (real-time per-movie) ─────────────────────────────────────────

    /**
     * One-shot read of all members in the lobby.
     * Returns a map userId → LobbyMember so the UI can display usernames.
     */
    public void loadAllMembers(String roomCode, AllMembersCallback callback) {
        lobbiesRef.child(roomCode).child(Constants.NODE_MEMBERS)
                .get()
                .addOnCompleteListener(task -> {
                    Map<String, LobbyMember> result = new HashMap<>();
                    if (task.isSuccessful() && task.getResult().exists()) {
                        for (DataSnapshot snap : task.getResult().getChildren()) {
                            LobbyMember member = snap.getValue(LobbyMember.class);
                            if (snap.getKey() != null && member != null) {
                                result.put(snap.getKey(), member);
                            }
                        }
                    }
                    callback.onLoaded(result);
                });
    }

    /**
     * Attaches a real-time ChildEventListener to
     *   lobbies/{roomCode}/votes/{movieId}/
     * and fires onVotesUpdated with the current full set of voterUIDs whenever
     * any user's vote is added or removed.
     *
     * Only one votes listener is active at a time — old ones are detached
     * automatically when this is called again for a new movie.
     *
     * @param roomCode lobby room code
     * @param movieId  TMDB movie ID (int)
     * @param callback notified on every change with the live set of voter UIDs
     */
    public void listenVotesForMovie(String roomCode, int movieId, VotesCallback callback) {
        detachVotesListener(); // always clean up the previous movie's listener first

        final java.util.Set<String> voters = new java.util.LinkedHashSet<>();
        activeVotesRef = lobbiesRef
                .child(roomCode)
                .child(Constants.NODE_VOTES)
                .child(String.valueOf(movieId));

        activeVotesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snap, String prev) {
                if (snap.getKey() != null) {
                    voters.add(snap.getKey());
                    callback.onVotesUpdated(new java.util.LinkedHashSet<>(voters));
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snap) {
                voters.remove(snap.getKey());
                callback.onVotesUpdated(new java.util.LinkedHashSet<>(voters));
            }
            @Override public void onChildChanged(@NonNull DataSnapshot s, String p) {}
            @Override public void onChildMoved(@NonNull DataSnapshot s, String p) {}
            @Override public void onCancelled(@NonNull DatabaseError e) {
                Logger.e(TAG, "listenVotesForMovie cancelled: " + e.getMessage());
            }
        };
        activeVotesRef.addChildEventListener(activeVotesListener);
        Logger.d(TAG, "Listening to votes for movie " + movieId);
    }

    // ── Load More Page Sync ──────────────────────────────────────────────────────

    public interface PageCallback {
        /** Called with the new page number whenever the host increments it. */
        void onPageChanged(int page);
    }

    private ValueEventListener activePageListener;
    private DatabaseReference   activePageRef;

    /**
     * Host calls this to broadcast the new TMDB page number to all lobby members.
     * Writes lobbies/{roomCode}/currentPage = page.
     */
    public void setCurrentPage(String roomCode, int page) {
        lobbiesRef.child(roomCode).child(Constants.NODE_CURRENT_PAGE).setValue(page);
        Logger.d(TAG, "Host set currentPage → " + page);
    }

    /**
     * One-shot read of the current TMDB page number for a lobby.
     * Returns 0 if the node is missing or an error occurs.
     */
    public void getCurrentPage(String roomCode, PageCallback callback) {
        lobbiesRef.child(roomCode)
                .child(Constants.NODE_CURRENT_PAGE)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || !task.getResult().exists()) {
                        callback.onPageChanged(0);
                        return;
                    }
                    Integer page = task.getResult().getValue(Integer.class);
                    callback.onPageChanged(page != null ? page : 0);
                });
    }

    /**
     * All devices call this to listen for page changes pushed by the host.
     * Fires immediately with the current value, then on every subsequent change.
     */
    public void listenCurrentPage(String roomCode, PageCallback callback) {
        detachPageListener();
        activePageRef = lobbiesRef.child(roomCode).child(Constants.NODE_CURRENT_PAGE);
        activePageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (snap.exists()) {
                    Integer page = snap.getValue(Integer.class);
                    if (page != null) callback.onPageChanged(page);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Logger.e(TAG, "listenCurrentPage cancelled: " + e.getMessage());
            }
        };
        activePageRef.addValueEventListener(activePageListener);
    }

    private void detachPageListener() {
        if (activePageRef != null && activePageListener != null) {
            activePageRef.removeEventListener(activePageListener);
            activePageListener = null;
            activePageRef      = null;
        }
    }
}
