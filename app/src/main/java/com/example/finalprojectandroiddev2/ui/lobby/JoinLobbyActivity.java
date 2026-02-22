package com.example.finalprojectandroiddev2.ui.lobby;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.repository.FirebaseRepository;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Join lobby screen.
 *
 * Flow:
 *  1. User enters a 6-character room code
 *  2. Validates format (6 alphanumeric chars)
 *  3. Calls FirebaseRepository.joinLobby() which also checks:
 *     - Lobby exists
 *     - Status is "waiting" (not already swiping/matched)
 *     - Not full (max 10 members)
 *  4. On success → navigates to LobbyActivity (as non-host)
 *  5. On failure → shows inline error message
 */
public class JoinLobbyActivity extends BaseActivity {

    private static final String TAG = "CineMatch.JoinLobby";

    private FirebaseRepository firebaseRepo;
    private String currentUserId;
    private String currentUsername;

    // ── Views ──────────────────────────────────────────────────────────────────
    private TextInputLayout         inputRoomCodeLayout;
    private TextInputEditText       editRoomCode;
    private TextView                textError;
    private MaterialButton          btnJoin;
    private MaterialButton          btnBack;
    private LinearProgressIndicator progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);
        applyEdgeToEdgeInsets(R.id.container_join_lobby);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { finish(); return; }
        currentUserId   = user.getUid();
        currentUsername = user.getDisplayName() != null
                ? user.getDisplayName()
                : user.getEmail() != null ? user.getEmail() : "User";

        firebaseRepo = FirebaseRepository.getInstance();

        bindViews();
        setupButtons();
    }

    // ── Setup ──────────────────────────────────────────────────────────────────

    private void bindViews() {
        inputRoomCodeLayout = findViewById(R.id.input_room_code);
        editRoomCode        = findViewById(R.id.edit_room_code);
        textError           = findViewById(R.id.text_error);
        btnJoin             = findViewById(R.id.btn_join);
        btnBack             = findViewById(R.id.btn_back);
        // If the layout has a progress indicator, wire it up; otherwise this will be null
        progressLoading     = findViewById(R.id.progress_loading);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
        btnJoin.setOnClickListener(v -> attemptJoin());
    }

    // ── Join Flow ──────────────────────────────────────────────────────────────

    private void attemptJoin() {
        hideError();

        String rawCode = editRoomCode.getText() != null
                ? editRoomCode.getText().toString().trim().toUpperCase()
                : "";

        // Client-side format validation
        if (TextUtils.isEmpty(rawCode) || rawCode.length() != 6 || !rawCode.matches("[A-Z0-9]{6}")) {
            showError(getString(R.string.error_invalid_code));
            return;
        }

        setLoading(true);

        firebaseRepo.joinLobby(
                rawCode, currentUserId, currentUsername,
                new FirebaseRepository.SimpleCallback() {
                    @Override public void onSuccess() {
                        setLoading(false);
                        Logger.d(TAG, "Joined lobby: " + rawCode);
                        navigateToLobby(rawCode);
                    }
                    @Override public void onFailure(String message) {
                        setLoading(false);
                        showError(mapErrorMessage(message));
                    }
                }
        );
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    private void navigateToLobby(String code) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra(LobbyActivity.EXTRA_ROOM_CODE, code);
        intent.putExtra(LobbyActivity.EXTRA_IS_HOST, false);
        startActivity(intent);
        finish();
    }

    // ── UI helpers ─────────────────────────────────────────────────────────────

    private void showError(String message) {
        textError.setText(message);
        textError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        textError.setVisibility(View.GONE);
    }

    private void setLoading(boolean loading) {
        btnJoin.setEnabled(!loading);
        if (progressLoading != null) {
            progressLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Maps raw Firebase/validation error strings to user-friendly string resources.
     */
    private String mapErrorMessage(String raw) {
        if (raw == null) return getString(R.string.msg_lobby_not_found);
        if (raw.contains("not found"))        return getString(R.string.msg_lobby_not_found);
        if (raw.contains("already started"))  return getString(R.string.msg_lobby_already_started);
        if (raw.contains("full"))             return getString(R.string.msg_lobby_full);
        return raw;
    }
}
