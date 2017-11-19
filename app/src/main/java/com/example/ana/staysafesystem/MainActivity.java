package com.example.ana.staysafesystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final String userNumber = sharedPref.getString("userNumber", null);
        final String userName = sharedPref.getString("userName", null);
        if(userNumber != null && userName != null) {
            nextScreen();
        } else {
            setContentView(R.layout.activity_main);
            Button next = (Button) findViewById(R.id.login);
            next.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    EditText numberEditText = (EditText) findViewById(R.id.userNumber);
                    EditText nameEditText = (EditText) findViewById(R.id.userName);
                    String number = numberEditText.getText().toString();
                    String name = nameEditText.getText().toString();

                    if (!number.contentEquals("") && !name.contentEquals("")) {
                        SharedPreferences.Editor ed = sharedPref.edit();
                        ed.putString("userNumber", number);
                        ed.putString("userName", name);
                        ed.commit();
                        nextScreen();
                    }
                }
            });
        }
    }

    void nextScreen() {
        Intent myIntent = new Intent(this, UserModeActivity.class);
        startActivity(myIntent);
    }

}
