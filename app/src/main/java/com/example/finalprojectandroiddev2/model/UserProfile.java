package com.example.finalprojectandroiddev2.model;

/**
 * User profile stored in Firebase Realtime Database under "users/{uid}".
 * Holds additional app-specific details beyond Firebase Auth (email/password).
 */
public class UserProfile {

    private String uid;
    private String name;
    private String gender;
    private String birthday; // ISO-8601 string (e.g., 1998-05-21)
    private String email;

    public UserProfile() {
        // Required empty constructor for Firebase
    }

    public UserProfile(String uid, String name, String gender, String birthday, String email) {
        this.uid = uid;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

