package com.example.ana.staysafesystem.gui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.Person;
import com.example.ana.staysafesystem.processor.Processor;
import com.example.ana.staysafesystem.processor.ServerConnectionService;

import java.util.ArrayList;

/* Code adapted from:
* https://www.android-examples.com/get-show-all-phone-contacts-into-listview-in-android/
* */
public class FriendsListActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Person> allContacts;
    ArrayList<Person> currentFriends;
    ArrayAdapter<Person> arrayAdapter;
    Cursor cursor;
    String name, phoneNumber;
    public  static final int RequestPermissionCode = 1;
    Button editButton; // edit friends list
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friends_list);
        setTitle("Lista de amigos");

        listView = findViewById(R.id.friendsList);
        enableRuntimePermission();
        viewCurrentFriendsList();

        allContacts = new ArrayList<>();

        editButton = findViewById(R.id.editList);
        saveButton = findViewById(R.id.saveList);
        saveButton.setVisibility(View.INVISIBLE);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button editButton = findViewById(R.id.editList);
                editButton.setVisibility(View.INVISIBLE);
                Button saveButton = findViewById(R.id.saveList);
                saveButton.setVisibility(View.VISIBLE);

                getContactsIntoArrayList();
                // TODO check if users (friends list) has account in the system
                arrayAdapter = new ArrayAdapter<Person>(
                        view.getContext(),
                        R.layout.contact_list_item,
                        allContacts) {
                    @Override
                    public View getView(final int position, View view, ViewGroup viewGroup) {
                        LayoutInflater inflater = getLayoutInflater();
                        View row;
                        if (view == null) {
                            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        }
                        row = inflater.inflate(R.layout.contact_list_item, viewGroup, false);

                        Person person = allContacts.get(position);
                        if(Processor.getInstance().isInCacheFriendsList(person)) {
                            float[] green = {143.4f, 0.88f, 0.88f};
                            row.setBackgroundColor(Color.HSVToColor(green));// this set background color
                        } else {
                            row.setBackgroundColor(Color.WHITE);
                        }
                        TextView textView = row.findViewById(R.id.itemContent);
                        //textView.setText(person.viewContactString());
                        textView.setText(person.toString());
                        return row;
                    }
                };

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        Person person = allContacts.get(position);
                        Processor.getInstance().updateFriendInList(view.getContext(), person);
                        // swap color of item in listview
                        boolean isInCache = Processor.getInstance().isInCacheFriendsList(person);
                        float[] green = {143.4f, 0.88f, 0.88f};
                        int color = isInCache ? Color.HSVToColor(green) : Color.WHITE;
                        view.setBackgroundColor(color);
                    }
                });
                listView.setAdapter(arrayAdapter);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("GUARDIANS", "save button pressed");
                Processor.getInstance().saveCacheFriendsList(view.getContext(), currentFriends);
                viewCurrentFriendsList();
                Button editList = findViewById(R.id.editList);
                editList.setVisibility(View.VISIBLE);
                Button saveList = findViewById(R.id.saveList);
                saveList.setVisibility(View.INVISIBLE);
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
                UtilGUI.dialog(view.getContext(), person.viewContactString());
            }
        });
        listView.setAdapter(arrayAdapter);
    }

    public void getContactsIntoArrayList(){
        cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null, null, null);

        allContacts.clear();
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Person person = new Person(name, phoneNumber);
            if (phoneNumber.length() >= 8 && !allContacts.contains(person)) {
                allContacts.add(person);
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
                    UtilGUI.dialog(this, "Esse aplicativo não tem permissão para " +
                            "acessar seus contatos.");
                }
                break;
        }
    }


}
