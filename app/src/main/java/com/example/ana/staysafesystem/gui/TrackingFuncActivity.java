package com.example.ana.staysafesystem.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;

public class TrackingFuncActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        setTitle("Rastreamento do aparelho");
        Button tracking = findViewById(R.id.trackingFinished);
        tracking.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TextView textView = findViewById(R.id.email);
                String email = textView.toString();
                if(!email.contentEquals("")) {
                    Util.setPref(view.getContext(),
                            "emailTracking", "email", email);
                    Util.changeScreen(view.getContext(), ProtectedUserActivity.class);
                }
            }
        });

    }
}
