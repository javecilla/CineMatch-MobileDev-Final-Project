package com.example.finalprojectandroiddev2.data.model;

/**
 * POJO representing a single member inside a lobby on Firebase Realtime Database.
 *
 * Firebase path: lobbies/{roomCode}/members/{userId}/
 *   username  : String
 *   joinedAt  : long   (System.currentTimeMillis())
 *   isHost    : boolean
 */
public class LobbyMember {

    private String username;
    private long joinedAt;
    private boolean isHost;

    /** Required by Firebase deserializer. */
    public LobbyMember() {}

    public LobbyMember(String username, long joinedAt, boolean isHost) {
        this.username = username;
        this.joinedAt = joinedAt;
        this.isHost   = isHost;
    }

    public String getUsername()  { return username; }
    public long   getJoinedAt() { return joinedAt; }
    public boolean isHost()      { return isHost; }

    public void setUsername(String username)  { this.username = username; }
    public void setJoinedAt(long joinedAt)   { this.joinedAt = joinedAt; }
    public void setHost(boolean host)         { isHost = host; }
}
