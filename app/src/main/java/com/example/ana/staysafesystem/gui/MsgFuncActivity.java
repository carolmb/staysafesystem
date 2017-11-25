package com.example.ana.staysafesystem.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.MetaMsg;
import com.example.ana.staysafesystem.processor.Processor;

public class MsgFuncActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_func);
        setTitle("Mensagem");
        final int buttonId = getIntent().getIntExtra("buttonPressed", 0);

        Button saveMsg = findViewById(R.id.finishedMsg);
        saveMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.metaMsg);
                String msgContent = editText.getText().toString();
                if(msgContent.length() > 0) {

                    CheckBox local = findViewById(R.id.local);
                    CheckBox heartbeat = findViewById(R.id.heartbeat);
                    CheckBox fall = findViewById(R.id.fall);
                    CheckBox audio = findViewById(R.id.audio);
                    MetaMsg metaMsg = new MetaMsg(msgContent,
                            local.isChecked(), heartbeat.isChecked(), fall.isChecked(), audio.isChecked());

                    Processor.getInstance().setMsgSettings(view.getContext(), metaMsg);
                    Processor.getInstance().setButtonFunc(view.getContext(), buttonId, "msg");
                    UtilGUI.changeScreen(view.getContext(), ProtectedUserActivity.class);
                } else {
                    UtilGUI.dialog(view.getContext(), "Você deve inserir uma mensagem.");
                }
            }

        });

        Button editList = findViewById(R.id.editListShorcut);
        editList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilGUI.changeScreen(view.getContext(), FriendsListActivity.class);
            }
        });

    }
}
