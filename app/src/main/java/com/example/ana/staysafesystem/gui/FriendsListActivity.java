package com.example.ana.staysafesystem.gui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ana.staysafesystem.R;

import java.util.ArrayList;

/* Code from:
* https://www.android-examples.com/get-show-all-phone-contacts-into-listview-in-android/
* */

public class FriendsListActivity extends AppCompatActivity {

    ListView listView ;
    ArrayList<String> StoreContacts ;
    ArrayAdapter<String> arrayAdapter ;
    Cursor cursor ;
    String name, phonenumber ;
    public  static final int RequestPermissionCode  = 1 ;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friends_list);

        listView = (ListView) findViewById(R.id.listview1);
        button = (Button) findViewById(R.id.button1);
        StoreContacts = new ArrayList<String>();
        EnableRuntimePermission();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetContactsIntoArrayList();

                arrayAdapter = new ArrayAdapter<String>(
                        v.getContext(),
                        R.layout.contact_list_item,
                        StoreContacts
                );
                listView.setAdapter(arrayAdapter);
            }
        });

    }

    public void GetContactsIntoArrayList(){
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            StoreContacts.add(name + " "  + ":" + " " + phonenumber);
        }
        cursor.close();
    }

    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.dialog("Permissão show. Temos acesso.", this);
                } else {
                    Util.dialog("Deu ruim.", this);
                }
                break;
        }
    }


}
