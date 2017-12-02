package com.example.ana.staysafesystem.processor;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ana.staysafesystem.data.DataInternalStorage;
import com.example.ana.staysafesystem.data.MetaMsg;
import com.example.ana.staysafesystem.data.Person;

import java.util.ArrayList;

/**
 * Created by ana on 19/11/17.
 */
public class Processor {

    public static boolean isConnected;

    DataInternalStorage<ArrayList<Person>> internalStorageFriends;
    DataInternalStorage<MetaMsg> internalStorageMsgSettings;
    ArrayList<Person> cacheFriendsList;

    String serverIp = "192.168.0.108";
    int serverPort = 5555;

    static private Processor instance;
    private Processor() {
        internalStorageFriends = new DataInternalStorage<>("friendsList");
        internalStorageMsgSettings = new DataInternalStorage<>("msgSettings");
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
        ServerConnectionService.loginServer(getProtectedUser(context));
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

    public void setMsgSettings(Context context, MetaMsg msgSettings) {
        internalStorageMsgSettings.saveObj(context, msgSettings);
    }

    public MetaMsg getMsgSettings(Context context) {
        return internalStorageMsgSettings.getObj(context);
    }

    public void clearMode(Context context) {
        setPref(context, "mode", "mode", null);
    }

    public void logout(Context context) {
        setProtectedUser(context, new Person(null, null));
        setUserMode(context, null);
        setButtonFunc(context, 1, null);
        setButtonFunc(context, 2, null);
    }

    public String getButtonFunc(Context context, int id) {
        return getPref(context, "button", "b" + id);
    }

    public void setButtonFunc(Context context, int id, String value) {
        setPref(context, "button", "b" + id, value);
    }

    public String getUserMode(Context context) {
        return getPref(context, "mode", "mode");
    }

    public void setUserMode(Context context, String mode){
        setPref(context, "mode", "mode", mode);
    }

    public Person getProtectedUser(Context context) {
        String userName = getPref(context, "login", "userName");
        String userNumber = getPref(context, "login", "userNumber");
        return new Person(userName, userNumber);
    }

    public void setProtectedUser(Context context, Person person) {
        setPref(context, "login", "userName", person.getName());
        setPref(context, "login", "userNumber", person.getPhoneNumber());
    }

    public boolean isValidUser(Context context) {
        Person protectedUser = getProtectedUser(context);
        if(protectedUser.isValid()) {
            return true;
        }
        return false;
    }

    public void setCallFriend(Context context, String phoneNumber, String audioType) {
        setPref(context, "callFriend", "friendPhoneNumber", phoneNumber);
        setPref(context, "callFriend", "isRecorded", audioType);
    }

    public String getCallFriend(Context context) {
        String number = getPref(context, "callFriend", "friendPhoneNumber");
        return number;
    }

    public void setEmail(Context context, String email){
        setPref(context, "emailTracking", "email", email);
    }

    private void setPref(Context context, String pref, String field, String value) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(pref, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(field, value);
        ed.commit();
    }

    private String getPref(Context context, String pref, String field) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(pref, Context.MODE_PRIVATE);
        return sharedPref.getString(field, null);
    }

}
