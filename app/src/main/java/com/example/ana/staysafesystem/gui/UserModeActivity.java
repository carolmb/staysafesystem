package com.example.ana.staysafesystem.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ana.staysafesystem.R;

public class UserModeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mode);

        Button guardianUser = findViewById(R.id.guardian);
        guardianUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String newMode = "guardian";
                Util.setPref(view.getContext(), "mode", "mode", newMode);
                nextScreen(newMode);
            }
        });

        Button secureUser = (Button) findViewById(R.id.prot);
        secureUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String newMode = "secure";
                Util.setPref(view.getContext(), "mode", "mode", newMode);
                nextScreen(newMode);
            }
        });

        if(!isValidUser()) {
            Util.changeScreen(this, LoginActivity.class);
        }

        String mode = Util.getPref(this, "mode", "mode");
        if(mode != null) {
            nextScreen(mode);
        }
    }

    void nextScreen(String mode) {
        if(mode.contentEquals("guardian")) {
            Util.changeScreen(this, GuardianUserActivity.class);
        } else {
            Util.changeScreen(this, ProtectedUserActivity.class);
        }
    }

    boolean isValidUser() {
        String userName = Util.getPref(this, "login", "userName");
        String userNumber = Util.getPref(this, "login", "userNumber");
        if(userNumber != null && userName != null) {
            return true;
        }
        return false;
    }
}
