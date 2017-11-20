package com.example.ana.staysafesystem.gui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.Person;
import com.example.ana.staysafesystem.processor.Processor;

import java.util.ArrayList;

/* Code from:
* https://www.android-examples.com/get-show-all-phone-contacts-into-listview-in-android/
* */

public class FriendsListActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Person> allContacts;
    ArrayList<Person> currentFriends;
    ArrayAdapter<Person> arrayAdapter;
    Cursor cursor;
    String name, phoneNumber;
    public  static final int RequestPermissionCode  = 1 ;
    Button editButton; // edit friends list
    Button saveButton;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friends_list);

        listView = findViewById(R.id.friendsList);
        editButton = findViewById(R.id.editList);
        saveButton = findViewById(R.id.saveList);
        allContacts = new ArrayList<>();
        enableRuntimePermission();

        viewCurrentFriendsList();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContactsIntoArrayList();
                // TODO check if users (friends list) has account in the system
                arrayAdapter = new ArrayAdapter<>(
                        view.getContext(),
                        R.layout.contact_list_item,
                        allContacts
                );

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        Person person = allContacts.get((int) id);
                        Processor.getInstance().updateFriendInList(view.getContext(), person);
                        // swap color of item in listview
                        boolean isInCache = Processor.getInstance().isInCacheFriendsList(person);
                        int color = isInCache ? Color.BLUE : Color.WHITE;
                        parent.getChildAt((int) id).setBackgroundColor(color);
                    }
                });
                listView.setAdapter(arrayAdapter);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Processor.getInstance().saveCacheFriendsList(view.getContext());
                viewCurrentFriendsList();
            }
        });
    }

    public void viewCurrentFriendsList() {
        currentFriends = Processor.getInstance().getCurrentFriendsList(this);
        if(currentFriends == null) {
            return;
        }
        arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.contact_list_item,
                currentFriends
        );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                Person person = currentFriends.get((int)id);
                Util.dialog(view.getContext(), person.viewContactString());
            }
        });
        listView.setAdapter(arrayAdapter);
    }

    public void getContactsIntoArrayList(){
        cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null, null, null);

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (phoneNumber.length() >= 8) {
                allContacts.add(new Person(name, phoneNumber));
            }
        }
        cursor.close();
    }

    public void enableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(this,"Essa permissão nos garante acesso aos contatos do seu celular.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (!(PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED)) {
                    Util.dialog(this, "Esse aplicativo não tem permissão para " +
                            "acessar seus contatos.");
                }
                break;
        }
    }


}
