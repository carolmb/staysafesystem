package com.example.ana.staysafesystem.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.Processor;

public class TrackingFuncActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        setTitle("Rastreamento do aparelho");
        final int buttonId = getIntent().getIntExtra("buttonPressed", 0);
        Log.d("BUTTON ID", buttonId + "");

        Button tracking = findViewById(R.id.trackingFinished);
        tracking.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TextView textView = findViewById(R.id.email);
                String email = textView.getText().toString();
                if(email.length() > 0) {
                    Processor.getInstance().setEmail(view.getContext(), email);
                    Processor.getInstance().setButtonFunc(view.getContext(), buttonId, "track");
                    UtilGUI.changeScreen(view.getContext(), ProtectedUserActivity.class);
                } else {
                    UtilGUI.dialog(view.getContext(), "Você deve inserir um e-mail válido.");
                }
            }
        });

    }
}
