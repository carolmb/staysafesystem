package com.example.ana.staysafesystem.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.Person;
import com.example.ana.staysafesystem.processor.Processor;

public class ProfileActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Perfil");

        Button listFriends = findViewById(R.id.friendsList);
        listFriends.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                UtilGUI.changeScreen(view.getContext(), FriendsListActivity.class);
            }
        });
        Button history = findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                UtilGUI.changeScreen(view.getContext(), HistoryActivity.class);
            }
        });
        Button cancel = findViewById(R.id.cancelAccount);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                UtilGUI.changeScreen(view.getContext(), CancelAccountActivity.class);
            }
        });
        TextView textView = findViewById(R.id.profileName);
        Person user = Processor.getInstance().getProtectedUser(this);
        textView.setText(user.toString());
    }

}
