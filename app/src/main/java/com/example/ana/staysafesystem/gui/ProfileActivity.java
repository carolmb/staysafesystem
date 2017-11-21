package com.example.ana.staysafesystem.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.Person;
import com.example.ana.staysafesystem.processor.Processor;

public class ProfileActivity extends Activity {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);
            Button button = findViewById(R.id.listfriends);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    UtilGUI.changeScreen(view.getContext(), FriendsListActivity.class);
                }
            });
            TextView textView = findViewById(R.id.profileName);
            Person user = Processor.getInstance().getProtectedUser(this);
            textView.setText(user.toString());
        }

}