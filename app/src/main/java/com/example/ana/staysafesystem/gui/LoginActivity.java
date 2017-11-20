package com.example.ana.staysafesystem.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.Processor;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Processor.getInstance().initInternalMemory(this);

        Button next = findViewById(R.id.login);
        next.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            EditText numberEditText = findViewById(R.id.userNumber);
            EditText nameEditText = findViewById(R.id.userName);
            String number = numberEditText.getText().toString();
            String name = nameEditText.getText().toString();
            if (!number.contentEquals("") && !name.contentEquals("")) {
                Util.setPref(view.getContext(), "login", "userName", name);
                Util.setPref(view.getContext(), "login", "userNumber", number);
                Util.changeScreen(view.getContext(), UserModeActivity.class);
            } else {
                Util.dialog(view.getContext(), "É necessário informar seu número celular e seu nome.");
            }
        }
        });

    }
}
