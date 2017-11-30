package com.example.ana.staysafesystem.gui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.Processor;

public class CallFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ligação para amigo especial");
        setContentView(R.layout.activity_call_friend);
        final int buttonId = getIntent().getIntExtra("buttonPressed", 0);
        enableRuntimePermission();
        Button callFriend = findViewById(R.id.callFriendFineshed);
        callFriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TextView textView = findViewById(R.id.friendPhoneNumber);
                String phone = textView.getText().toString();
                if(phone.length() >= 8) {
                    RadioButton radioButton = findViewById(R.id.recordedAudio);
                    String audioType;
                    if (radioButton.isChecked()) {
                        audioType = "recorded";
                    } else {
                        audioType = "live";
                    }
                    Processor.getInstance().setCallFriend(view.getContext(), phone, audioType);
                    Processor.getInstance().setButtonFunc(view.getContext(), buttonId, "call");
                    UtilGUI.changeScreen(view.getContext(), ProtectedUserActivity.class);
                } else {
                    UtilGUI.dialog(view.getContext(), "Você deve inserir um telefone válido.");
                }
            }
        });
    }


    public void enableRuntimePermission(){
        int RequestPermissionCode = 2;
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CALL_PHONE)) {
            Toast.makeText(this,"Essa permissão nos garante que possamos fazer " +
                    "a ligação por você.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case 2:
                if (!(PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED)) {
                    UtilGUI.dialog(this, "Esse aplicativo não tem permissão para " +
                            "acessar seus contatos.");
                }
                break;
        }
    }
}
