package com.example.ana.staysafesystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPref = getSharedPreferences("Mode", Context.MODE_PRIVATE);
        final String mode = sharedPref.getString("mode", null);
        if(mode != null) {
            nextScreen(mode);
        } else {
            setContentView(R.layout.activity_user_mode);
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
            Intent myIntent = new Intent(this, GuardianUserActivity.class);
            startActivity(myIntent);
        } else {
            Intent myIntent = new Intent(this, ProtectedUserActivity.class);
            startActivity(myIntent);
        }
    }
}
