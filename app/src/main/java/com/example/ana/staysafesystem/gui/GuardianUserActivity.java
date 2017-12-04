package com.example.ana.staysafesystem.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.Person;
import com.example.ana.staysafesystem.processor.Processor;

import java.util.ArrayList;

public class GuardianUserActivity extends AppCompatActivity {

    static ListView listView;
    static ArrayList<Person> currentFriends;
    static ArrayAdapter<Person> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian);
        setTitle("3S - Guardião");
        listView = findViewById(R.id.friends);
        protectedFriends();
    }

    public void protectedFriends() {

        currentFriends = Processor.getInstance().getProtectedFriends();
        arrayAdapter = new ArrayAdapter<Person>(
                this,
                R.layout.contact_list_item,
                currentFriends);
        listView.setAdapter(arrayAdapter);
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
                UtilGUI.dialog(this,
                        "Stay Safe System foi pensado com carinho para te ajudar.");
                break;

            case R.id.menuProfile:
                // TODO change ProfileActivity to another screen
                UtilGUI.changeScreen(this, ProfileActivity.class);
                break;

            case R.id.menuMode:
                // clean mode
                Processor.getInstance().clearMode(this);
                UtilGUI.changeScreen(this, UserModeActivity.class);
                break;

            case R.id.menuLogout:
                // logout + clean mode
                Processor.getInstance().logout(this);
                UtilGUI.changeScreen(this, LoginActivity.class);
                break;

        }
        return true;
    }

}
