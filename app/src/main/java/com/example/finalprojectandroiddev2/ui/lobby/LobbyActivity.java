package com.example.finalprojectandroiddev2.ui.lobby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.model.LobbyMember;
import com.example.finalprojectandroiddev2.data.repository.FirebaseRepository;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.swiping.SwipingActivity;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Lobby screen (for members who joined via a room code).
 *
 * Receives "room_code" and "is_host" from Intent extras.
 * - Displays live member list via FirebaseRepository.listenMembers()
 * - Host sees "Start Swiping" (enabled when >= 2 members)
 * - Status listener navigates all non-host members when session starts
 * - "Leave Lobby" triggers host-transfer or last-member cleanup
 */
public class LobbyActivity extends BaseActivity {

    private static final String TAG = "CineMatch.Lobby";

    public static final String EXTRA_ROOM_CODE = "room_code";
    public static final String EXTRA_IS_HOST   = "is_host";

    private String roomCode;
    private String currentUserId;
    private boolean isHost;
    private boolean sessionStarted = false; // guard against double navigation

    private FirebaseRepository firebaseRepo;
    private MemberAdapter memberAdapter;

    private final List<MemberAdapter.MemberItem> memberItems = new ArrayList<>();

    // ── View references ────────────────────────────────────────────────────────
    private TextView                  textRoomCode;
    private RecyclerView              recyclerMembers;
    private LinearProgressIndicator   progressLoading;
    private MaterialButton            btnStartSwiping;
    private MaterialButton            btnLeaveLobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        applyEdgeToEdgeInsets(R.id.container_lobby);

        roomCode      = getIntent().getStringExtra(EXTRA_ROOM_CODE);
        isHost        = getIntent().getBooleanExtra(EXTRA_IS_HOST, false);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";

        firebaseRepo = FirebaseRepository.getInstance();

        bindViews();
        setupRecyclerView();
        setupButtons();
        displayRoomCode();
        attachFirebaseListeners();
    }

    // ── Setup ──────────────────────────────────────────────────────────────────

    private void bindViews() {
        textRoomCode    = findViewById(R.id.text_room_code);
        recyclerMembers = findViewById(R.id.recycler_members);
        progressLoading = findViewById(R.id.progress_loading);
        btnStartSwiping = findViewById(R.id.btn_start_swiping);
        btnLeaveLobby   = findViewById(R.id.btn_leave_lobby);
    }

    private void setupRecyclerView() {
        memberAdapter = new MemberAdapter();
        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerMembers.setAdapter(memberAdapter);
    }

    private void setupButtons() {
        // "Start Swiping" — visible and enabled only for the host
        if (isHost) {
            btnStartSwiping.setVisibility(View.VISIBLE);
            btnStartSwiping.setEnabled(false); // enabled once >= 2 members
            btnStartSwiping.setOnClickListener(v -> startSwipingSession());
        } else {
            btnStartSwiping.setVisibility(View.GONE);
        }

        btnLeaveLobby.setOnClickListener(v -> confirmLeave());
    }

    private void displayRoomCode() {
        if (roomCode != null) {
            textRoomCode.setText(roomCode);
        }
    }

    // ── Firebase Listeners ─────────────────────────────────────────────────────

    private void attachFirebaseListeners() {
        progressLoading.setVisibility(View.VISIBLE);

        firebaseRepo.listenMembers(roomCode, new FirebaseRepository.MembersCallback() {
            @Override public void onMemberAdded(String userId, LobbyMember member) {
                progressLoading.setVisibility(View.GONE);
                updateOrAddMember(userId, member);
            }
            @Override public void onMemberChanged(String userId, LobbyMember member) {
                updateOrAddMember(userId, member);
            }
            @Override public void onMemberRemoved(String userId) {
                removeMemberFromList(userId);
            }
        });

        // Status listener — navigate non-host members when swiping starts
        firebaseRepo.listenLobbyStatus(roomCode, status -> {
            if (Constants.LOBBY_STATUS_SWIPING.equals(status) && !sessionStarted) {
                sessionStarted = true;
                navigateToSwiping();
            }
        });
    }

    // ── Member list helpers ────────────────────────────────────────────────────

    /** Maps from userId → MemberItem for O(1) lookup. */
    private final java.util.LinkedHashMap<String, MemberAdapter.MemberItem>
            memberMap = new java.util.LinkedHashMap<>();

    private void updateOrAddMember(String userId, LobbyMember member) {
        MemberAdapter.MemberItem item =
                new MemberAdapter.MemberItem(member.getUsername(), member.isHost(), true);
        memberMap.put(userId, item);
        refreshAdapter();
        updateStartButton();
    }

    private void removeMemberFromList(String userId) {
        memberMap.remove(userId);
        refreshAdapter();
        updateStartButton();
    }

    private void refreshAdapter() {
        memberAdapter.setMembers(new ArrayList<>(memberMap.values()));
    }

    private void updateStartButton() {
        if (isHost) {
            btnStartSwiping.setEnabled(memberMap.size() >= 2);
        }
    }

    // ── Start Swiping ──────────────────────────────────────────────────────────

    private void startSwipingSession() {
        btnStartSwiping.setEnabled(false);
        firebaseRepo.setLobbyStatus(roomCode, Constants.LOBBY_STATUS_SWIPING);
        // Status listener above will fire for all devices (including host)
        sessionStarted = true;
        navigateToSwiping();
    }

    private void navigateToSwiping() {
        Intent intent = new Intent(this, SwipingActivity.class);
        intent.putExtra(EXTRA_ROOM_CODE, roomCode);
        intent.putExtra(EXTRA_IS_HOST, isHost);
        startActivity(intent);
        finish();
    }

    // ── Leave Lobby ────────────────────────────────────────────────────────────

    private void confirmLeave() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.msg_leave_lobby_confirm)
                .setPositiveButton(android.R.string.ok, (d, w) -> leaveLobby())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void leaveLobby() {
        firebaseRepo.removeMember(roomCode, currentUserId, new FirebaseRepository.SimpleCallback() {
            @Override public void onSuccess() {
                navigateToHome();
            }
            @Override public void onFailure(String message) {
                Logger.e(TAG, "leaveLobby failed: " + message);
                Toast.makeText(LobbyActivity.this, message, Toast.LENGTH_SHORT).show();
                navigateToHome();
            }
        });
    }

    private void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    @Override
    protected void onDestroy() {
        firebaseRepo.detachListeners();
        super.onDestroy();
    }
}
