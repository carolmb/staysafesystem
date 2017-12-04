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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
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
    static String serverIp = "192.168.0.108";

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
        update();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {}

    public void update() {
        final Person user = Processor.getInstance().getProtectedUser(this);
        final Context context = this;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(5000);
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.e("MSG DO SERVER", response);
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success) { // if there is update
                                        String title = jsonResponse.getString("title");
                                        String subtitle = jsonResponse.getString("subtitle");
                                        String content = jsonResponse.getString("content");
                                        String from = jsonResponse.getString("from_phone");
                                        Msg msg = new Msg(new Person("Amigo", from), title, subtitle, content);
                                        createNotification(msg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.e("VOLLEY", volleyError.getMessage());
                                volleyError.printStackTrace();
                            }
                        };

                        UpdateRequest updateRequest =
                                new UpdateRequest(user, responseListener, errorListener);
                        RequestQueue queue = Volley.newRequestQueue(context);
                        queue.add(updateRequest);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        t.start();
    }

    private void createNotification(Msg msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.help)
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