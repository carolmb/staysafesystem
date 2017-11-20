package com.example.ana.staysafesystem.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;

public class SettingsActivity extends Activity {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            Button button = findViewById(R.id.listfriends);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Util.changeScreen(view.getContext(), FriendsListActivity.class);
                }
            });
            TextView textView = findViewById(R.id.profileName);
            String userName = Util.getPref(this, "login", "userName");
            textView.setText(userName);
        }

}