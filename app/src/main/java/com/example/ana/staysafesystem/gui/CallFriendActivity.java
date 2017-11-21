package com.example.ana.staysafesystem.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;

public class CallFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ligação para amigo especial");
        setContentView(R.layout.activity_call_friend);
        Button callFriend = findViewById(R.id.callFriendFineshed);
        callFriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TextView textView = findViewById(R.id.friendPhoneNumber);
                RadioButton radioButton = findViewById(R.id.recordedAudio);
                String audioType = null;
                if(radioButton.isChecked()) {
                    audioType = "recorded";
                } else {
                    audioType = "live";
                }
                Util.setPref(view.getContext(), "callFriend", "friendPhoneNumber", textView.toString());
                Util.setPref(view.getContext(), "callFriend", "isRecorded", audioType);
                Util.changeScreen(view.getContext(), ProtectedUserActivity.class);
            }
        });
    }
}
