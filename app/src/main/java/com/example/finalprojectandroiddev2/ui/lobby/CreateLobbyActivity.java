package com.example.finalprojectandroiddev2.ui.lobby;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.model.LobbyMember;
import com.example.finalprojectandroiddev2.data.repository.FirebaseRepository;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.home.HomeActivity;
import com.example.finalprojectandroiddev2.ui.swiping.SwipingActivity;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.example.finalprojectandroiddev2.utils.Logger;
import com.example.finalprojectandroiddev2.utils.RoomCodeGenerator;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Create lobby screen.
 *
 * Flow:
 *  1. Generates a unique room code via RoomCodeGenerator
 *  2. Creates the lobby in Firebase (host is automatically added as first member)
 *  3. Listens to Firebase members/ in real-time → updates MemberAdapter
 *  4. Enables "Start Swiping" once >= 2 members are present (host-only)
 *  5. Share button sends room code via Android's share sheet
 *  6. On back / destroy → removeMember() to clean up Firebase
 */
public class CreateLobbyActivity extends BaseActivity {

    private static final String TAG = "CineMatch.CreateLobby";

    private String roomCode;
    private String currentUserId;
    private String currentUsername;
    private boolean sessionStarted = false;

    private FirebaseRepository firebaseRepo;
    private MemberAdapter memberAdapter;

    private final java.util.LinkedHashMap<String, MemberAdapter.MemberItem>
            memberMap = new java.util.LinkedHashMap<>();

    // ── Views ──────────────────────────────────────────────────────────────────
    private TextView       textRoomCode;
    private RecyclerView   recyclerMembers;
    private TextView       textWaiting;
    private MaterialButton btnShare;
    private MaterialButton btnStartSwiping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        applyEdgeToEdgeInsets(R.id.container_create_lobby);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { finish(); return; }
        currentUserId = user.getUid();
        currentUsername = user.getDisplayName() != null
                ? user.getDisplayName()
                : user.getEmail() != null ? user.getEmail() : "User";

        firebaseRepo = FirebaseRepository.getInstance();

        bindViews();
        setupRecyclerView();
        setupButtons();

        // Show placeholder while generating
        textRoomCode.setText(getString(R.string.msg_generating_code));
        btnStartSwiping.setEnabled(false);

        generateCodeAndCreateLobby();
    }

    // ── Setup ──────────────────────────────────────────────────────────────────

    private void bindViews() {
        textRoomCode    = findViewById(R.id.text_room_code);
        recyclerMembers = findViewById(R.id.recycler_members);
        textWaiting     = findViewById(R.id.text_waiting_for_members);
        btnShare        = findViewById(R.id.btn_share_room_code);
        btnStartSwiping = findViewById(R.id.btn_start_swiping);
    }

    private void setupRecyclerView() {
        memberAdapter = new MemberAdapter();
        recyclerMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerMembers.setAdapter(memberAdapter);
    }

    private void setupButtons() {
        btnShare.setOnClickListener(v -> shareRoomCode());
        btnStartSwiping.setOnClickListener(v -> startSwipingSession());
    }

    // ── Room Code Generation ───────────────────────────────────────────────────

    private void generateCodeAndCreateLobby() {
        RoomCodeGenerator.generate(firebaseRepo, new RoomCodeGenerator.Callback() {
            @Override public void onCodeGenerated(String code) {
                roomCode = code;
                textRoomCode.setText(code);
                createLobbyInFirebase();
            }
            @Override public void onError(String message) {
                Toast.makeText(CreateLobbyActivity.this, message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    // ── Firebase Lobby Creation ────────────────────────────────────────────────

    private void createLobbyInFirebase() {
        firebaseRepo.createLobby(
                roomCode, currentUserId, currentUsername,
                new FirebaseRepository.SimpleCallback() {
                    @Override public void onSuccess() {
                        Logger.d(TAG, "Lobby created: " + roomCode);
                        attachFirebaseListeners();
                    }
                    @Override public void onFailure(String message) {
                        Toast.makeText(CreateLobbyActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        );
    }

    // ── Firebase Listeners ─────────────────────────────────────────────────────

    private void attachFirebaseListeners() {
        firebaseRepo.listenMembers(roomCode, new FirebaseRepository.MembersCallback() {
            @Override public void onMemberAdded(String userId, LobbyMember member) {
                updateOrAddMember(userId, member);
            }
            @Override public void onMemberChanged(String userId, LobbyMember member) {
                updateOrAddMember(userId, member);
            }
            @Override public void onMemberRemoved(String userId) {
                memberMap.remove(userId);
                refreshAdapter();
                updateStartButton();
            }
        });

        // Listen for status → when swiping starts, navigate as host to SwipingActivity
        firebaseRepo.listenLobbyStatus(roomCode, status -> {
            if (Constants.LOBBY_STATUS_SWIPING.equals(status) && !sessionStarted) {
                sessionStarted = true;
                navigateToSwiping();
            }
        });
    }

    // ── Member list helpers ────────────────────────────────────────────────────

    private void updateOrAddMember(String userId, LobbyMember member) {
        memberMap.put(userId, new MemberAdapter.MemberItem(
                member.getUsername(), member.isHost(), true));
        refreshAdapter();
        updateStartButton();
        textWaiting.setVisibility(memberMap.size() <= 1 ? View.VISIBLE : View.GONE);
    }

    private void refreshAdapter() {
        memberAdapter.setMembers(new ArrayList<>(memberMap.values()));
    }

    private void updateStartButton() {
        btnStartSwiping.setEnabled(memberMap.size() >= 2);
    }

    // ── Start Swiping ──────────────────────────────────────────────────────────

    private void startSwipingSession() {
        btnStartSwiping.setEnabled(false);
        firebaseRepo.setLobbyStatus(roomCode, Constants.LOBBY_STATUS_SWIPING);
        // Status listener above fires for all devices
    }

    private void navigateToSwiping() {
        Intent intent = new Intent(this, SwipingActivity.class);
        intent.putExtra(LobbyActivity.EXTRA_ROOM_CODE, roomCode);
        intent.putExtra(LobbyActivity.EXTRA_IS_HOST, true);
        startActivity(intent);
        finish();
    }

    // ── Share ──────────────────────────────────────────────────────────────────

    private void shareRoomCode() {
        if (roomCode == null) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Join my CineMatch lobby! Room code: " + roomCode);
        startActivity(Intent.createChooser(shareIntent, "Share Room Code"));
    }

    // ── Back / Leave ───────────────────────────────────────────────────────────

    @Override
    public void onBackPressed() {
        leaveLobby();
    }

    private void leaveLobby() {
        if (roomCode != null) {
            firebaseRepo.removeMember(roomCode, currentUserId, null);
        }
        firebaseRepo.detachListeners();
        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    protected void onDestroy() {
        firebaseRepo.detachListeners();
        super.onDestroy();
    }
}
