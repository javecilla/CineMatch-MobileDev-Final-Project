package com.example.finalprojectandroiddev2.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalprojectandroiddev2.BuildConfig;
import com.example.finalprojectandroiddev2.model.UserProfile;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Repository for user profile data stored in Firebase Realtime Database.
 *
 * Structure:
 * users/
 *   {uid}/
 *     name: "Jerome Avecilla"
 *     gender: "Male" | "Female" | "Other" | ...
 *     birthday: "1998-05-21" (ISO string)
 *     email: "user@example.com"
 */
public class UserRepository {

    private static final String TAG = Constants.TAG_FIREBASE;

    private static UserRepository instance;

    private final DatabaseReference usersRef;

    private UserRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(BuildConfig.FB_ROUTE_INSTANCE_URL);
        usersRef = database.getReference(Constants.NODE_USERS);
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    /**
     * Creates or updates a user profile at users/{uid}.
     */
    public void saveUserProfile(@NonNull UserProfile profile, @NonNull ProfileSaveCallback callback) {
        if (profile.getUid() == null || profile.getUid().isEmpty()) {
            callback.onError("User id is required");
            return;
        }

        usersRef.child(profile.getUid())
                .setValue(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Logger.d(TAG, "User profile saved for uid=" + profile.getUid());
                            callback.onSuccess();
                        } else {
                            Exception e = task.getException();
                            Logger.e(TAG, "Failed to save user profile for uid=" + profile.getUid(), e);
                            callback.onError(e != null ? e.getMessage() : "Failed to save user profile");
                        }
                    }
                });
    }

    /**
     * Fetches the user profile at users/{uid}.
     */
    public void getUserProfile(@NonNull String uid, @NonNull ProfileLoadCallback callback) {
        usersRef.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserProfile profile = snapshot.getValue(UserProfile.class);
                            Logger.d(TAG, "Loaded user profile for uid=" + uid);
                            callback.onSuccess(profile);
                        } else {
                            Logger.d(TAG, "No user profile found for uid=" + uid);
                            callback.onSuccess(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Logger.e(TAG, "getUserProfile cancelled for uid=" + uid, error.toException());
                        callback.onError(error.getMessage());
                    }
                });
    }

    /**
     * Callback for profile save operations.
     */
    public interface ProfileSaveCallback {
        void onSuccess();

        void onError(String errorMessage);
    }

    /**
     * Callback for profile load operations.
     */
    public interface ProfileLoadCallback {
        void onSuccess(@Nullable UserProfile profile);

        void onError(String errorMessage);
    }
}

