package com.example.finalprojectandroiddev2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.repository.AuthRepository;
import com.example.finalprojectandroiddev2.data.repository.UserRepository;
import com.example.finalprojectandroiddev2.model.UserProfile;
import com.example.finalprojectandroiddev2.ui.auth.LoginActivity;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;
import com.example.finalprojectandroiddev2.ui.lobby.CreateLobbyActivity;
import com.example.finalprojectandroiddev2.ui.lobby.JoinLobbyActivity;
import com.example.finalprojectandroiddev2.utils.Utils;

/**
 * Home screen (main page) with profile display, Create Lobby and Join Lobby options.
 */
public class HomeActivity extends BaseActivity {

    private TextView textUsername;
    private TextView textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        applyEdgeToEdgeInsets(R.id.container_home);

        textUsername = findViewById(R.id.text_username);
        textEmail = findViewById(R.id.text_email);

        loadUserProfile();

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            AuthRepository.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });

        findViewById(R.id.btn_create_lobby).setOnClickListener(v ->
                startActivity(new Intent(this, CreateLobbyActivity.class)));

        findViewById(R.id.btn_join_lobby).setOnClickListener(v ->
                startActivity(new Intent(this, JoinLobbyActivity.class)));
    }

    /**
     * Loads user profile from Realtime DB and sets display name and email. Falls back to auth email if no profile.
     */
    private void loadUserProfile() {
        if (AuthRepository.getInstance().getCurrentUser() == null) {
            textUsername.setText(R.string.title_home);
            textEmail.setText("");
            return;
        }
        String uid = AuthRepository.getInstance().getCurrentUser().getUid();
        String authEmail = AuthRepository.getInstance().getCurrentUser().getEmail();
        textEmail.setText(Utils.orDefault(authEmail, ""));

        UserRepository.getInstance().getUserProfile(uid, new UserRepository.ProfileLoadCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                if (profile != null && Utils.isNotBlank(profile.getName())) {
                    textUsername.setText(profile.getName());
                } else {
                    textUsername.setText(Utils.orDefault(authEmail, getString(R.string.title_home)));
                }
            }

            @Override
            public void onError(String errorMessage) {
                textUsername.setText(Utils.orDefault(authEmail, getString(R.string.title_home)));
            }
        });
    }
}
