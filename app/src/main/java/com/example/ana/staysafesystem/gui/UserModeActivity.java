package com.example.ana.staysafesystem.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.LocalService;
import com.example.ana.staysafesystem.processor.Processor;

public class UserModeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mode);
        setTitle("3S - StaySafeSystem");

        Intent i = new Intent(this, LocalService.class);
        startService(i);

        Button guardianUser = findViewById(R.id.guardian);
        guardianUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Processor.getInstance().setUserMode(view.getContext(), "menu");
                nextScreen("menu");
            }
        });

        Button secureUser = findViewById(R.id.prot);
        secureUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Processor.getInstance().setUserMode(view.getContext(), "protected");
                nextScreen("protected");
            }
        });

        if(!Processor.getInstance().isValidUser(this)) {
            UtilGUI.changeScreen(this, LoginActivity.class);
        }

        String mode = Processor.getInstance().getUserMode(this);
        if(mode != null) {
            nextScreen(mode);
        }
    }

    void nextScreen(String mode) {
        if(mode.contentEquals("menu")) {
            UtilGUI.changeScreen(this, GuardianUserActivity.class);
        } else {
            UtilGUI.changeScreen(this, ProtectedUserActivity.class);
        }
    }
}
