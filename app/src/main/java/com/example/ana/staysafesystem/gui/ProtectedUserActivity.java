package com.example.ana.staysafesystem.gui;

import android.content.Context;
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
                Util.changeScreen(view.getContext(), AddFuncActivity.class);
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
                Util.changeScreen(this, SettingsActivity.class);
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
