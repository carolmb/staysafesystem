package com.example.ana.staysafesystem.gui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.ana.staysafesystem.R;

public class AddFuncActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_func);
        setTitle("Adicionar funcionalidade");

        final int buttonId = getIntent().getIntExtra("buttonPressed", 0);

        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Context context = view.getContext();
                RadioButton msg = findViewById(R.id.msg);
                RadioButton track = findViewById(R.id.track);
                RadioButton call = findViewById(R.id.call);

                if(msg.isChecked()) {
                    Intent intent = new Intent(context, MsgFuncActivity.class);
                    intent.putExtra("buttonPressed", buttonId);
                    context.startActivity(intent);
                } else if(track.isChecked()) {
                    Intent intent = new Intent(context, TrackingFuncActivity.class);
                    intent.putExtra("buttonPressed", buttonId);
                    context.startActivity(intent);
                } else if(call.isChecked()) {
                    Intent intent = new Intent(context, CallFriendActivity.class);
                    intent.putExtra("buttonPressed", buttonId);
                    context.startActivity(intent);
                } else {
                    UtilGUI.dialog(context, "É necessário selecionar uma função.");
                }
            }
        });
    }

}
