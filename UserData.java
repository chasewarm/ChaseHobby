package com.example.chasehobby;

import java.util.List;

public class UserData {
    private User user;
    private List<String> genres;
    private MediaPreferences preferences;

    public UserData() {}

    public UserData(User user, List<String> genres, MediaPreferences preferences) {
        this.user = user;
        this.genres = genres;
        this.preferences = preferences;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public MediaPreferences getPreferences() { return preferences; }
    public void setPreferences(MediaPreferences preferences) { this.preferences = preferences; }

    public boolean hasGenres() {
        return genres != null && !genres.isEmpty();
    }

    public boolean hasPreferences() {
        return preferences != null;
    }

    public String getUsername() {
        return user != null ? user.getUsername() : "";
    }

    public int getUserId() {
        return user != null ? user.getId() : -1;
    }
}