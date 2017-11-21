package com.example.ana.staysafesystem.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.Processor;
import com.example.ana.staysafesystem.data.Msg;

public class MsgFuncActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_func);
        setTitle("Configurações da mensagem");
        final int buttonId = getIntent().getIntExtra("buttonPressed", 0);

        Button saveMsg = findViewById(R.id.finishedMsg);
        saveMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = findViewById(R.id.msgDescription);
                String msgContent = textView.toString();

                CheckBox local = findViewById(R.id.local);
                CheckBox heartbeat = findViewById(R.id.heartbeat);
                CheckBox fall = findViewById(R.id.fall);
                CheckBox audio = findViewById(R.id.audio);
                Msg msg = new Msg(msgContent, local.isChecked(), heartbeat.isChecked(), fall.isChecked(), audio.isChecked());

                Processor.getInstance().saveMsgFunc(view.getContext(), msg);
                Util.setPref(view.getContext(), "button","b" + buttonId, "msg");
                Util.changeScreen(view.getContext(), ProtectedUserActivity.class);
            }

        });

        Button editList = findViewById(R.id.editListShorcut);
        editList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.changeScreen(view.getContext(), FriendsListActivity.class);
            }
        });

    }
}
