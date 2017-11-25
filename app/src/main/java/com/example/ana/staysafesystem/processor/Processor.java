package com.example.ana.staysafesystem.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.DataInternalStorage;
import com.example.ana.staysafesystem.data.MetaMsg;
import com.example.ana.staysafesystem.data.Msg;
import com.example.ana.staysafesystem.data.Person;
import com.example.ana.staysafesystem.data.SensorsInfo;
import com.example.ana.staysafesystem.gui.FriendAskingHelpActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by ana on 19/11/17.
 */
public class Processor {

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
        loginServer(getProtectedUser(context));
    }

    private void loginServer(Person p) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", ACTION.LOGIN);
            json.put("name", p.getName());
            json.put("phone", p.getPhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendSocket(serverIp, serverPort, json.toString());
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

    private MetaMsg getMsgSettings(Context context) {
        return internalStorageMsgSettings.getObj(context);
    }

    public void buttonPressed(Context context, JSONObject json) {
        int buttonId = -1;
        try {
            buttonId = json.getInt("buttonId");
            String buttonFunc = getButtonFunc(context, buttonId);
            doAction(context, buttonFunc, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doAction(Context context, String func, JSONObject json) {
        if(func.contentEquals("msg")) {
            sendMsg(context, json);
        } else if(func.contentEquals("call")) {
            callFriend();
        } else if(func.contentEquals("track")) {
            // TODO
        }
    }

    private void sendMsg(Context context, JSONObject json) {
        SensorsInfo sensorsInfo = new SensorsInfo(json);
        MetaMsg metaMsg = getMsgSettings(context);
        Person user = getProtectedUser(context);
        ArrayList<Person> friends = getCurrentFriendsList(context);
        final Msg msg = new Msg(user, sensorsInfo, metaMsg, friends);
        sendSocket(serverIp, serverPort, msg.toJson().toString());
    }

    private void callFriend() {
        // TODO
    }

    private void sendSocket(final String ip, final int port, final String content) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket soc = new Socket(ip, port);
                    PrintWriter writer = new PrintWriter(soc.getOutputStream());
                    writer.write(content);
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
        });
        t.start();
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
