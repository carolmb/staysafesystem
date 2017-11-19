package com.example.ana.staysafesystem.gui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ana.staysafesystem.R;

public class UserModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isValidUser()) {
            Util.changeScreen(this, LoginActivity.class);
        }

        final SharedPreferences sharedPref = getSharedPreferences("Mode", Context.MODE_PRIVATE);
        final String mode = sharedPref.getString("mode", null);
        setContentView(R.layout.activity_user_mode);
        if(mode != null) {
            nextScreen(mode);
        } else {
            Button guardianUser = (Button) findViewById(R.id.guardian);
            guardianUser.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String mode = "guardian";
                    SharedPreferences.Editor ed = sharedPref.edit();
                    ed.putString("mode", mode);
                    ed.commit();
                    nextScreen(mode);
                }
            });

            Button secureUser = (Button) findViewById(R.id.secure);
            secureUser.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String mode = "secure";
                    SharedPreferences.Editor ed = sharedPref.edit();
                    ed.putString("mode", mode);
                    ed.commit();
                    nextScreen(mode);
                }
            });
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
        final SharedPreferences sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE);
        final String userNumber = sharedPref.getString("userNumber", null);
        final String userName = sharedPref.getString("userName", null);
        if(userNumber != null && userName != null) {
            return true;
        }
        return false;
    }
}
