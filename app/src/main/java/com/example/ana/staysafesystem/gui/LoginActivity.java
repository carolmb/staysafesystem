package com.example.ana.staysafesystem.gui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.Person;
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
                Context context = view.getContext();
                EditText nameEditText = findViewById(R.id.userName);
                EditText phoneEditText = findViewById(R.id.userNumber);

                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();

                Person user = new Person(name, phone);
                if (user.isValid()) {
                    Processor.getInstance().setProtectedUser(context, user);
                    UtilGUI.changeScreen(context, UserModeActivity.class);
                } else {
                    UtilGUI.dialog(context,
                            "É necessário informar seu número celular e seu nome.");
                }
        }
        });

    }
}
