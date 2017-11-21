package com.example.ana.staysafesystem.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.Processor;

public class AddFuncActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_func);
        setTitle("Adicionar funcionalidade");

        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                RadioButton msg = findViewById(R.id.msg);
                RadioButton track = findViewById(R.id.track);
                RadioButton call = findViewById(R.id.call);

                if(msg.isChecked()) {
                    Util.changeScreen(view.getContext(), MsgFuncActivity.class);
                } else if(track.isChecked()) {
                    Util.changeScreen(view.getContext(), TrackingFuncActivity.class);
                } else if(call.isChecked()) {
                    Util.changeScreen(view.getContext(), CallFriendActivity.class);
                } else {
                    Util.dialog(view.getContext(), "É necessário selecionar uma função.");
                }
            }
        });
    }

}
