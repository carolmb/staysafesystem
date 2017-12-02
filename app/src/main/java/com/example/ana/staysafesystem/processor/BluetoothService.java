package com.example.ana.staysafesystem.processor;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.example.ana.staysafesystem.gui.ProtectedUserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by ana on 01/12/17.
 */

public class BluetoothService extends Service implements Bluetooth.CommunicationCallback {

    static public Bluetooth b;
    static boolean registered = false;

    static int serverPort = 5555;
    static String serverIp = "192.168.0.108";

    private NotificationManager mNM;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        location();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int id) {
        if(!registered) {
            Log.e("EITAAA", "ele ta falso");
            Bluetooth b = new Bluetooth(null);
            b.enableBluetooth();
            b.setCommunicationCallback(this);
            int pos = intent.getExtras().getInt("pos");

            b.connectToDevice(b.getPairedDevices().get(pos));
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
            registered = true;

        }
        return START_STICKY;
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        Person from = new Person("Pulseira", "");
        String title = "Conexão com bluetooth";
        String subtitle = "Sua conexão com bluetooth ocorreu com sucesso.";
        String content = "Agora você está conectado com sua pulseira.";
        createNotification(new Msg(from, title, subtitle, content));
    }

    private void createNotification(Msg msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.help)
                        .setContentTitle(msg.getTitle())
                        .setContentText(msg.getSubtitle());

        Intent resultIntent = new Intent(this, ProtectedUserActivity.class);
        resultIntent.putExtra("contentMsg", msg.getContent());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(FriendAskingHelpActivity.class);
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

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        b.connectToDevice(device);
    }

    private final Handler handler = new Handler();

    @Override
    public void onMessage(final String message) {
        try {
            buttonPressed(new JSONObject(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("EITAAA", message + " QUE ALEGRIA");
    }

    @Override
    public void onError(String message) {
        Log.e("Error: ", message);
    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Log.e("Error: ", message);
        Thread thread = (new Thread() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.connectToDevice(device);
                    }
                }, 2000);
            }
        });
        thread.run();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            Person from = new Person("Pulseira", "");
            String title = "Conexão com bluetooth quebrada";
            String subtitle = "Sua conexão com bluetooth não está mais ativa.";
            String content = "Tente se conectar novamente.";
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    if(registered) {
                        unregisterReceiver(mReceiver);
                        registered = false;
                    }
                    createNotification(new Msg(from, title, subtitle, content));
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    if(registered) {
                        unregisterReceiver(mReceiver);
                        registered = false;
                    }
                    createNotification(new Msg(from, title, subtitle, content));
                    break;
            }
        }
    }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
    return null;
    }

    public void buttonPressed(JSONObject json) {
        Log.e("BUTTON", "BUTTON PRESSED");
        int buttonId = -1;
        try {
            buttonId = json.getInt("buttonId");
            String buttonFunc = Processor.getInstance().getButtonFunc(this, buttonId);
            doAction(buttonFunc, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doAction(String func, JSONObject json) {
        if(func.contentEquals("msg")) {
            Log.e("BUTTON", "MSG");

            sendMsg(json);
        } else if(func.contentEquals("call")) {
            callFriend();
        } else if(func.contentEquals("track")) {
            // TODO
        }
    }

    private void sendMsg(JSONObject json) {
        Log.e("BUTTON", "sendMsg");
        SensorsInfo sensorsInfo = new SensorsInfo(json, this);
        MetaMsg metaMsg = Processor.getInstance().getMsgSettings(this);
        Person user = Processor.getInstance().getProtectedUser(this);
        ArrayList<Person> friends = Processor.getInstance().getCurrentFriendsList(this);
        final Msg msg = new Msg(user, sensorsInfo, metaMsg, friends);
        sendSocket(serverIp, serverPort, msg.toJson().toString());
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

    static public Location location;

    private void location() {
        Log.e("LOCAL", "local");
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("LOCAL", "local atualizado");
                BluetoothService.location = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO ask in protected user activity
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
    }


}
