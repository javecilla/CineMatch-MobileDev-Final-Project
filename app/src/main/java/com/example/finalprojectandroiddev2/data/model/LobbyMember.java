package com.example.finalprojectandroiddev2.data.model;

/**
 * POJO representing a single member inside a lobby on Firebase Realtime Database.
 *
 * Firebase path: lobbies/{roomCode}/members/{userId}/
 *   username  : String
 *   gender    : String  (e.g. "Male", "Female", "Other")
 *   joinedAt  : long   (System.currentTimeMillis())
 *   isHost    : boolean
 */
public class LobbyMember {

    private String  username;
    private String  gender;
    private long    joinedAt;
    private boolean isHost;

    /** Required by Firebase deserializer. */
    public LobbyMember() {}

    public LobbyMember(String username, String gender, long joinedAt, boolean isHost) {
        this.username = username;
        this.gender   = gender;
        this.joinedAt = joinedAt;
        this.isHost   = isHost;
    }

    public String  getUsername() { return username; }
    public String  getGender()   { return gender;   }
    public long    getJoinedAt() { return joinedAt;  }
    public boolean isHost()      { return isHost;    }

    public void setUsername(String username) { this.username = username; }
    public void setGender(String gender)     { this.gender   = gender;   }
    public void setJoinedAt(long joinedAt)   { this.joinedAt = joinedAt; }
    public void setHost(boolean host)        { isHost = host; }
}
