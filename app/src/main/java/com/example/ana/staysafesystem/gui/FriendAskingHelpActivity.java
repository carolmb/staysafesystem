package com.example.ana.staysafesystem.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;

public class FriendAskingHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_asking_help);

        TextView msgContent = findViewById(R.id.friendMsg);
        String content = getIntent().getStringExtra("contentMsg").toString();
        msgContent.setText(content);

        Button exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilGUI.changeScreen(view.getContext(), GuardianUserActivity.class);
            }
        });
    }
}
