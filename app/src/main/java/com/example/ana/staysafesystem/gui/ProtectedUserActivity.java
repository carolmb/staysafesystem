package com.example.ana.staysafesystem.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ana.staysafesystem.R;

public class ProtectedUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_protected_user);
        String b1 = Util.getPref(this, "button", "b1");
        String b2 = Util.getPref(this, "button", "b2");

        TextView textView1 = findViewById(R.id.func1);
        ImageButton imageButton1 = findViewById(R.id.imageButton1);
        configButton(b1, textView1, imageButton1, 1);

        TextView textView2 = findViewById(R.id.func2);
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        configButton(b2, textView2, imageButton2, 2);
    }

    void configButton(String button, TextView textView, ImageButton imageButton, int id) {
        if(button != null) {
            showFunc(button, textView, imageButton, id);
        } else {
            nullFunc(textView, imageButton, id);
        }
    }

    void nullFunc(TextView textView, ImageButton imageButton, final int id) {
        textView.setText("Não configurado");
        imageButton.setImageResource(android.R.drawable.ic_input_add);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddFuncActivity.class);
                intent.putExtra("buttonPressed", id);
                view.getContext().startActivity(intent);
            }
        });
    }

    void showFunc(String button, TextView textView, ImageButton imageButton, final int id) {
        textView.setText(button);
        imageButton.setImageResource(android.R.drawable.ic_menu_delete);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Util.setPref(view.getContext(), "button", "b" + id, null);
                ImageButton imageButton;
                TextView textView;
                if(id == 1) {
                    imageButton = findViewById(R.id.imageButton1);
                    textView = findViewById(R.id.func1);
                } else {
                    imageButton = findViewById(R.id.imageButton2);
                    textView = findViewById(R.id.func2);
                }
                nullFunc(textView, imageButton, id);
                //imageButton.setImageResource(android.R.drawable.ic_input_add);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuAbout:
                Toast.makeText(this,
                        "Stay Safe System foi pensado com carinho para te ajudar.",
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuSettings:
                Util.changeScreen(this, ProfileActivity.class);
                break;

            case R.id.menuMode:
                // clean mode
                Util.setPref(this, "mode", "mode", null);
                Util.changeScreen(this, UserModeActivity.class);
                break;

            case R.id.menuLogout:
                // logout + clean mode
                Util.setPref(this, "login", "userName", null);
                Util.setPref(this, "login", "userNumber", null);
                Util.setPref(this, "mode", "mode", null);
                Util.changeScreen(this, LoginActivity.class);
                break;

        }
        return true;
    }
}
