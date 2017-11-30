package com.example.ana.staysafesystem.processor;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import com.example.ana.staysafesystem.data.MetaMsg;
import com.example.ana.staysafesystem.data.Msg;
import com.example.ana.staysafesystem.data.Person;
import com.example.ana.staysafesystem.data.SensorsInfo;
import com.example.ana.staysafesystem.gui.FriendAskingHelpActivity;

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

    private NotificationManager mNM;
    int clientPort = 5561;
    static int serverPort = 5555;
    static String serverIp = "192.168.0.108";

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = 1;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        ServerConnectionService getService() {
            return ServerConnectionService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        waitServerMsg();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {}

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

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
                        createNotification(new Msg(json));
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

    public void callFriend() {
        String number = Processor.getInstance().getCallFriend(this);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        }
    }

    public void buttonPressed(Context context, JSONObject json) {
        int buttonId = -1;
        try {
            buttonId = json.getInt("buttonId");
            String buttonFunc = Processor.getInstance().getButtonFunc(context, buttonId);
            doAction(buttonFunc, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doAction(String func, JSONObject json) {
        if(func.contentEquals("msg")) {
            sendMsg(json);
        } else if(func.contentEquals("call")) {
            callFriend();
        } else if(func.contentEquals("track")) {
            // TODO
        }
    }

    private void sendMsg(JSONObject json) {
        SensorsInfo sensorsInfo = new SensorsInfo(json);
        MetaMsg metaMsg = Processor.getInstance().getMsgSettings(this);
        Person user = Processor.getInstance().getProtectedUser(this);
        ArrayList<Person> friends = Processor.getInstance().getCurrentFriendsList(this);
        final Msg msg = new Msg(user, sensorsInfo, metaMsg, friends);
        sendSocket(serverIp, serverPort, msg.toJson().toString());
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

    public static Handler handler;

}