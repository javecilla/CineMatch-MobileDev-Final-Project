package com.example.finalprojectandroiddev2.utils;

import com.example.finalprojectandroiddev2.data.repository.FirebaseRepository;

import java.util.Locale;
import java.util.Random;

/**
 * Generates unique 6-character alphanumeric room codes and verifies uniqueness
 * against Firebase before returning.
 */
public class RoomCodeGenerator {

    public interface Callback {
        void onCodeGenerated(String roomCode);
        void onError(String message);
    }

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int    MAX_RETRIES = 5;
    private static final Random RANDOM = new Random();

    private RoomCodeGenerator() {}

    /**
     * Generates a unique room code that does not already exist in Firebase.
     * Retries up to MAX_RETRIES times before giving up.
     */
    public static void generate(FirebaseRepository repo, Callback callback) {
        tryGenerate(repo, callback, 0);
    }

    private static void tryGenerate(FirebaseRepository repo, Callback callback, int attempt) {
        if (attempt >= MAX_RETRIES) {
            callback.onError("Could not generate a unique room code. Please try again.");
            return;
        }

        String code = randomCode();

        repo.lobbyExists(code, exists -> {
            if (exists) {
                // Collision — retry with a different code
                tryGenerate(repo, callback, attempt + 1);
            } else {
                callback.onCodeGenerated(code);
            }
        });
    }

    /** Generates a random 6-character uppercase alphanumeric string. */
    private static String randomCode() {
        StringBuilder sb = new StringBuilder(Constants.ROOM_CODE_LENGTH);
        for (int i = 0; i < Constants.ROOM_CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
