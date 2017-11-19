package com.example.ana.staysafesystem.gui;

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

        final RadioButton msg = (RadioButton) findViewById(R.id.msg);
        final RadioButton track = (RadioButton) findViewById(R.id.track);
        final RadioButton call = (RadioButton) findViewById(R.id.call);

        msg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                track.setChecked(false);
                call.setChecked(false);
            }
        });
        track.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                msg.setChecked(false);
                call.setChecked(false);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                msg.setChecked(false);
                track.setChecked(false);
            }
        });

        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(msg.isChecked()) {
                    Util.changeScreen(view.getContext(), MsgFuncActivity.class);
                } else if(track.isChecked()) {
                    Util.changeScreen(view.getContext(), TrackBackActivity.class);
                } else if(call.isChecked()) {
                    Util.changeScreen(view.getContext(), CallFuncActivity.class);
                } else {
                    Util.dialog("É necessário selecionar uma função.", view.getContext());
                }
            }
        });
    }

}
