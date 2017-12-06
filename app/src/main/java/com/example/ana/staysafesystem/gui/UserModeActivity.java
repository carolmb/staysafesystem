package com.example.ana.staysafesystem.gui;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.BluetoothService;
import com.example.ana.staysafesystem.processor.ServerConnectionService;
import com.example.ana.staysafesystem.processor.Processor;

public class UserModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mode);
        setTitle("3S - StaySafeSystem");

        Intent server = new Intent(this, ServerConnectionService.class);
        startService(server);

        Processor.getInstance().initInternalMemory(this);

        Button guardianUser = findViewById(R.id.guardian);
        guardianUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Processor.getInstance().setUserMode(view.getContext(), "guardian");
                nextScreen("guardian");
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
        if(mode.contentEquals("guardian")) {
            UtilGUI.changeScreen(this, GuardianUserActivity.class);
        } else {
            if(BluetoothService.registered) {
                UtilGUI.changeScreen(this, ProtectedUserActivity.class);
            } else {
                UtilGUI.changeScreen(this, SelectPairActivity.class);
            }
        }
    }

}
