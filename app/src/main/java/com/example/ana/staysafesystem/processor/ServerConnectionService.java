package com.example.ana.staysafesystem.processor;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.Call;
import com.example.ana.staysafesystem.data.MetaMsg;
import com.example.ana.staysafesystem.data.Msg;
import com.example.ana.staysafesystem.data.Person;
import com.example.ana.staysafesystem.data.SensorsInfo;
import com.example.ana.staysafesystem.gui.FriendAskingHelpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by ana on 24/11/17.
 */

public class ServerConnectionService extends Service {

    private Binder binder;

    private NotificationManager mNM;
    int clientPort = 5561;
    static int serverPort = 5555;
    static String serverIp = "10.9.99.27";

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends android.os.Binder {
        public ServerConnectionService getService() {
            return ServerConnectionService.this;
        }
    }

    @Override
    public void onCreate() {
        binder = new Binder();
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        waitServerMsg();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {}

    public void waitServerMsg() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(clientPort);

                    while(true){
                        Socket socket = server.accept();
                        Scanner scanner = new Scanner(socket.getInputStream());
                        String msg = scanner.nextLine();
                        Log.d("SOCKET MSG", msg);
                        JSONObject json = new JSONObject(msg);
                        doAction(json);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void doAction(JSONObject json) {
        int code = 0;
        try {
            code = json.getInt("action");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (code) {
            case ACTION.MSG:
                createNotification(new Msg(json));
                break;
            case ACTION.HIST:
                updateCalls(json);
                break;
            case ACTION.UPDATE:
                getCalls(Processor.getInstance().getProtectedUser(this));
                break;
            case ACTION.PROTECTED:
                setProtectedFriends(json);
                // TODO

        }
    }
    private void setProtectedFriends(JSONObject json) {
        ArrayList<Person> protectedFriends = new ArrayList<Person>();
        try {
            JSONArray array = json.getJSONArray("prot");
            for(int i = 0; i < array.length(); i++) {
                Person p = new Person(array.getJSONObject(i));
                protectedFriends.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Processor.getInstance().setProtectedFriends(protectedFriends);
    }

    private void updateCalls(JSONObject json) {
        JSONArray array = null;
        try {
            array = json.getJSONArray("hist");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(array == null) {
            return;
        }
        ArrayList<Call> calls = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                calls.add(new Call(current));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Processor.getInstance().updateCallsCache(calls);
    }

    private void createNotification(Msg msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.minilogo)
                        .setContentTitle(msg.getTitle())
                        .setContentText(msg.getSubtitle());

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, FriendAskingHelpActivity.class);
        resultIntent.putExtra("contentMsg", msg.getContent());

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(FriendAskingHelpActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mNM.notify(999, mBuilder.build());
    }

    public static void getProtectedFriends(Person p) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", ACTION.PROTECTED);
            json.put("name", p.getName());
            json.put("phone", p.getPhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendSocket(serverIp, serverPort, json.toString());
    }

    public static void getCalls(Person p) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", ACTION.HIST);
            json.put("name", p.getName());
            json.put("phone", p.getPhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendSocket(serverIp, serverPort, json.toString());
    }

    public static void sendGuardians(ArrayList<Person> guardians) {
        JSONObject jsonMsg = new JSONObject();
        JSONArray arrayGuardians = new JSONArray();
        try {
            for(Person p: guardians) {
                arrayGuardians.put(p.toJson());
            }
            jsonMsg.put("action", ACTION.GUARDIANS);
            jsonMsg.put("friends", arrayGuardians);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void loginServer(Person p) {
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

    private static void sendSocket(final String ip, final int port, final String content) {
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

}