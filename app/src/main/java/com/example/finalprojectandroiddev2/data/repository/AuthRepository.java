package com.example.finalprojectandroiddev2.data.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.finalprojectandroiddev2.model.User;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Repository for Firebase Authentication operations.
 * Handles sign in, sign up, sign out, and auth state management.
 */
public class AuthRepository {

    private static AuthRepository instance;
    private final FirebaseAuth firebaseAuth;

    private AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    /**
     * Get current authenticated user, or null if not signed in.
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Convert FirebaseUser to app User model.
     */
    public User getCurrentUserModel() {
        FirebaseUser firebaseUser = getCurrentUser();
        if (firebaseUser == null) {
            return null;
        }
        User user = new User();
        user.setUid(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setDisplayName(firebaseUser.getDisplayName());
        if (firebaseUser.getPhotoUrl() != null) {
            user.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
        }
        return user;
    }

    /**
     * Sign in with email and password.
     *
     * @param email    User email
     * @param password User password
     * @param callback Result callback
     */
    public void signIn(@NonNull String email, @NonNull String password,
                       @NonNull AuthCallback callback) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            callback.onError("Email and password cannot be empty");
            return;
        }

        Logger.d(Constants.TAG_AUTH, "Signing in user: " + email);
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                Logger.i(Constants.TAG_AUTH, "Sign in successful: " + user.getUid());
                                callback.onSuccess(getCurrentUserModel());
                            } else {
                                Logger.e(Constants.TAG_AUTH, "Sign in successful but user is null");
                                callback.onError("Sign in failed: user is null");
                            }
                        } else {
                            Exception exception = task.getException();
                            String errorMessage = getErrorMessage(exception);
                            Logger.e(Constants.TAG_AUTH, "Sign in failed: " + errorMessage, exception);
                            callback.onError(errorMessage);
                        }
                    }
                });
    }

    /**
     * Create new account with email and password.
     *
     * @param email    User email
     * @param password User password
     * @param callback Result callback
     */
    public void signUp(@NonNull String email, @NonNull String password,
                       @NonNull AuthCallback callback) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            callback.onError("Email and password cannot be empty");
            return;
        }

        if (password.length() < 6) {
            callback.onError("Password must be at least 6 characters");
            return;
        }

        Logger.d(Constants.TAG_AUTH, "Signing up user: " + email);
        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                Logger.i(Constants.TAG_AUTH, "Sign up successful: " + user.getUid());
                                callback.onSuccess(getCurrentUserModel());
                            } else {
                                Logger.e(Constants.TAG_AUTH, "Sign up successful but user is null");
                                callback.onError("Sign up failed: user is null");
                            }
                        } else {
                            Exception exception = task.getException();
                            String errorMessage = getErrorMessage(exception);
                            Logger.e(Constants.TAG_AUTH, "Sign up failed: " + errorMessage, exception);
                            callback.onError(errorMessage);
                        }
                    }
                });
    }

    /**
     * Sign out current user.
     */
    public void signOut() {
        Logger.d(Constants.TAG_AUTH, "Signing out user");
        firebaseAuth.signOut();
    }

    /**
     * Add auth state listener to monitor authentication changes.
     *
     * @param listener AuthStateListener
     */
    public void addAuthStateListener(FirebaseAuth.AuthStateListener listener) {
        firebaseAuth.addAuthStateListener(listener);
    }

    /**
     * Remove auth state listener.
     *
     * @param listener AuthStateListener
     */
    public void removeAuthStateListener(FirebaseAuth.AuthStateListener listener) {
        firebaseAuth.removeAuthStateListener(listener);
    }

    /**
     * Convert Firebase Auth exceptions to user-friendly error messages.
     */
    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException) exception;
            String errorCode = authException.getErrorCode();

            switch (errorCode) {
                case "ERROR_INVALID_EMAIL":
                    return "Invalid email address";
                case "ERROR_WRONG_PASSWORD":
                    return "Incorrect password";
                case "ERROR_USER_NOT_FOUND":
                    return "No account found with this email";
                case "ERROR_USER_DISABLED":
                    return "This account has been disabled";
                case "ERROR_TOO_MANY_REQUESTS":
                    return "Too many requests. Please try again later";
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "An account with this email already exists";
                case "ERROR_WEAK_PASSWORD":
                    return "Password is too weak";
                case "ERROR_NETWORK_REQUEST_FAILED":
                    return "Network error. Please check your connection";
                default:
                    return "Authentication failed: " + errorCode;
            }
        }
        return exception != null ? exception.getMessage() : "Unknown error occurred";
    }

    /**
     * Callback interface for async auth operations.
     */
    public interface AuthCallback {
        void onSuccess(User user);

        void onError(String errorMessage);
    }
}
