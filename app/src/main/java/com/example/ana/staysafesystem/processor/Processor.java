package com.example.ana.staysafesystem.processor;

import android.content.Context;
import android.util.Log;

import com.example.ana.staysafesystem.data.DataInternalStorage;
import com.example.ana.staysafesystem.data.Person;

import java.util.ArrayList;

/**
 * Created by ana on 19/11/17.
 */

public class Processor {

    DataInternalStorage<ArrayList<Person>> internalStorageFriends;
    ArrayList<Person> cacheFriendsList;

    static private Processor instance;
    private Processor() {
        internalStorageFriends = new DataInternalStorage<ArrayList<Person>>("friendsList");
    }

    static public Processor getInstance() {
        if(instance != null) {
            return instance;
        } else {
            instance = new Processor();
            return instance;
        }
    }

    public void initInternalMemory(Context context) {
        internalStorageFriends.saveObj(context, new ArrayList<Person>());
        Log.d("bla", "bla");
    }

    public ArrayList<Person> getCurrentFriendsList(Context context) {
        if(cacheFriendsList == null) {
            cacheFriendsList = internalStorageFriends.getObj(context);
        }
        return cacheFriendsList;
    }

    public void updateFriendInList(Context context, Person person) {
        if(cacheFriendsList == null) {
            cacheFriendsList = internalStorageFriends.getObj(context);
        }
        if(!cacheFriendsList.contains(person)) {
            cacheFriendsList.add(person);
        } else {
            cacheFriendsList.remove(person);
        }
    }

    public boolean isInCacheFriendsList(Person person) {
        return cacheFriendsList.contains(person);
    }

    public void saveCacheFriendsList(Context context) {
        internalStorageFriends.saveObj(context, cacheFriendsList);
    }
}
