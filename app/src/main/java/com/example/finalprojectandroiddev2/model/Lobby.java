package com.example.finalprojectandroiddev2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Lobby model matching Firebase Realtime Database schema.
 * Structure: lobbies/{roomCode}/{hostId, createdAt, status, members, movies, matchedMovie}
 */
public class Lobby {

    private String roomCode;
    private String hostId;
    private Long createdAt;
    private String status;
    private Map<String, LobbyMember> members;
    private Map<String, LobbyMovie> movies;
    private MatchedMovie matchedMovie;

    public Lobby() {
        members = new HashMap<>();
        movies = new HashMap<>();
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, LobbyMember> getMembers() {
        return members;
    }

    public void setMembers(Map<String, LobbyMember> members) {
        this.members = members != null ? members : new HashMap<>();
    }

    public Map<String, LobbyMovie> getMovies() {
        return movies;
    }

    public void setMovies(Map<String, LobbyMovie> movies) {
        this.movies = movies != null ? movies : new HashMap<>();
    }

    public MatchedMovie getMatchedMovie() {
        return matchedMovie;
    }

    public void setMatchedMovie(MatchedMovie matchedMovie) {
        this.matchedMovie = matchedMovie;
    }

    /**
     * Member in a lobby: members/{userId}/{username, joinedAt, isHost}
     */
    public static class LobbyMember {
        private String username;
        private Long joinedAt;
        private boolean isHost;

        public LobbyMember() {
        }

        public LobbyMember(String username, Long joinedAt, boolean isHost) {
            this.username = username;
            this.joinedAt = joinedAt;
            this.isHost = isHost;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Long getJoinedAt() {
            return joinedAt;
        }

        public void setJoinedAt(Long joinedAt) {
            this.joinedAt = joinedAt;
        }

        public boolean isHost() {
            return isHost;
        }

        public void setHost(boolean host) {
            isHost = host;
        }
    }

    /**
     * Movie in lobby: movies/{movieId}/{tmdbId, title, posterPath, votes}
     */
    public static class LobbyMovie {
        private int tmdbId;
        private String title;
        private String posterPath;
        private Map<String, Boolean> votes;

        public LobbyMovie() {
            votes = new HashMap<>();
        }

        public LobbyMovie(int tmdbId, String title, String posterPath) {
            this();
            this.tmdbId = tmdbId;
            this.title = title;
            this.posterPath = posterPath;
        }

        public int getTmdbId() {
            return tmdbId;
        }

        public void setTmdbId(int tmdbId) {
            this.tmdbId = tmdbId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public Map<String, Boolean> getVotes() {
            return votes;
        }

        public void setVotes(Map<String, Boolean> votes) {
            this.votes = votes != null ? votes : new HashMap<>();
        }
    }

    /**
     * Matched movie: matchedMovie/{tmdbId, title, matchedAt}
     */
    public static class MatchedMovie {
        private int tmdbId;
        private String title;
        private String posterPath;
        private Long matchedAt;

        public MatchedMovie() {
        }

        public MatchedMovie(int tmdbId, String title, Long matchedAt) {
            this.tmdbId = tmdbId;
            this.title = title;
            this.matchedAt = matchedAt;
        }

        public int getTmdbId() {
            return tmdbId;
        }

        public void setTmdbId(int tmdbId) {
            this.tmdbId = tmdbId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public Long getMatchedAt() {
            return matchedAt;
        }

        public void setMatchedAt(Long matchedAt) {
            this.matchedAt = matchedAt;
        }
    }
}
