package com.example.finalprojectandroiddev2;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Application class for initializing Firebase Auth and other app-wide components.
 */
public class CineMatchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Firebase Auth is automatically initialized via google-services.json
        // No explicit initialization needed, but we can verify it's ready
        FirebaseAuth.getInstance();
    }
}
