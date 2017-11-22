package com.example.ana.staysafesystem.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ana.staysafesystem.data.DataInternalStorage;
import com.example.ana.staysafesystem.data.Msg;
import com.example.ana.staysafesystem.data.Person;

import java.util.ArrayList;

/**
 * Created by ana on 19/11/17.
 */
public class Processor {

    DataInternalStorage<ArrayList<Person>> internalStorageFriends;
    DataInternalStorage<Msg> internalStorageMsgSettings;
    ArrayList<Person> cacheFriendsList;

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

    public void setMsgSettings(Context context, Msg msgSettings) {
        internalStorageMsgSettings.saveObj(context, msgSettings);
    }

    private Msg getMsgSettings(Context context) {
        return internalStorageMsgSettings.getObj(context);
    }

    public void buttonPressed(final int id) {
        switch (id) {
            case 1:
                Thread t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doAction(id);
                    }
                });
                t1.start();
                break;
            case 2:
                Thread t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doAction(id);
                    }
                });
                t2.start();
                break;
        }
    }

    private void doAction(int id) {
        try {
            Socket soc = new Socket("192.168.0.108", 5560);
            PrintWriter writer = new PrintWriter(soc.getOutputStream());
            writer.write("Teste" + id);
            writer.flush();
            writer.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void clearMode(Context context) {
        setPref(context, "mode", "mode", null);
    }

    public void logout(Context context) {
        setPref(context, "login", "userName", null);
        setPref(context, "login", "userNumber", null);
        setPref(context, "mode", "mode", null);
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
