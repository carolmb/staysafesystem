package com.example.ana.staysafesystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProtectedUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protected_user);
        final SharedPreferences sharedPref = getSharedPreferences("Button", Context.MODE_PRIVATE);
        final String b1 = sharedPref.getString("b1", null);
        final String b2 = sharedPref.getString("b2", null);

        TextView textView1 = (TextView) findViewById(R.id.func1);
        ImageButton imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
        configButton(b1, textView1, imageButton1);

        TextView textView2 = (TextView) findViewById(R.id.func2);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        configButton(b2, textView2, imageButton2);
    }

    void configButton(String button, TextView textView, ImageButton imageButton) {
        if(button != null) {
            showFunc(button, textView, imageButton);
        } else {
            nullFunc(textView, imageButton);
        }
    }

    void nullFunc(TextView textView, ImageButton imageButton) {
        textView.setText("Não configurado");
        imageButton.setImageResource(android.R.drawable.ic_input_add);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
    }

    void showFunc(String button, TextView textView, ImageButton imageButton) {
        textView.setText(button);
        imageButton.setImageResource(android.R.color.transparent);
        imageButton.setImageResource(android.R.drawable.ic_menu_preferences);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
    }
}
