package com.example.finalprojectandroiddev2.ui.lobby;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.ui.base.BaseActivity;

/**
 * Join lobby screen: enter room code to join an existing lobby.
 * Firebase validation will be added in a later phase.
 */
public class JoinLobbyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);
        applyEdgeToEdgeInsets(R.id.container_join_lobby);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_join).setOnClickListener(v -> {
            String code = ((EditText) findViewById(R.id.edit_room_code)).getText().toString();
            if (TextUtils.isEmpty(code) || code.length() != 6) {
                TextView err = findViewById(R.id.text_error);
                err.setText(R.string.msg_invalid_room_code);
                err.setVisibility(View.VISIBLE);
                return;
            }
            Intent i = new Intent(this, LobbyActivity.class);
            i.putExtra(LobbyActivity.EXTRA_ROOM_CODE, code.toUpperCase());
            i.putExtra(LobbyActivity.EXTRA_IS_HOST, false);
            startActivity(i);
            finish();
        });
    }
}
