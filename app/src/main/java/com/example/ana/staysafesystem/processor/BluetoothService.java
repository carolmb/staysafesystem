package com.example.ana.staysafesystem.processor;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import com.example.ana.staysafesystem.gui.UtilGUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import me.aflak.bluetooth.Bluetooth;

/**
 * Created by ana on 01/12/17.
 */

public class BluetoothService extends Service implements Bluetooth.CommunicationCallback {

    static public Bluetooth b;
    static public boolean registered = false;

    static int serverPort = 5555;
    static String serverIp = "10.9.99.27";

    private NotificationManager mNM;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        location();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int id) {
        if(!registered) {
            Log.e("BLUETOOTH", "Registered está falso");
            Bluetooth b = new Bluetooth(null);
            b.enableBluetooth();
            b.setCommunicationCallback(this);
            int pos = intent.getExtras().getInt("pos");

            BluetoothDevice btDevice = b.getPairedDevices().get(pos);

            b.connectToDevice(btDevice);
        }
        return START_STICKY;
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered = true;

        Person from = new Person("Pulseira", "");
        String title = "Conexão com bluetooth";
        String subtitle = "Sua conexão com bluetooth ocorreu com sucesso.";
        String content = "Você está conectado com sua pulseira.";
        createNotification(new Msg(from, title, subtitle, content));
    }

    private void createNotification(Msg msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.help)
                        .setContentTitle(msg.getTitle())
                        .setContentText(msg.getSubtitle());

        Intent resultIntent = new Intent(this, ProtectedUserActivity.class);
        resultIntent.putExtra("btMsg", msg.getContent());

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
    }

    @Override
    public void onError(String message) {
        Log.e("Error: ", message);
        Person from = new Person("Pulseira", "");
        String title = "Conexão com bluetooth";
        String subtitle = "Sua conexão com bluetooth NÃO ocorreu com sucesso.";
        String content = "Você não está conectado a sua pulseira.";
        createNotification(new Msg(from, title, subtitle, content));
    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Log.e("Error: ", message);
        Person from = new Person("Pulseira", "");
        String title = "Conexão com bluetooth";
        String subtitle = "Sua conexão com bluetooth NÃO ocorreu com sucesso.";
        String content = "Você não está conectado a sua pulseira.";
        createNotification(new Msg(from, title, subtitle, content));
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
        int buttonId;
        try {
            buttonId = json.getInt("buttonId");
            String buttonFunc = Processor.getInstance().getButtonFunc(this, buttonId);
            doAction(buttonFunc, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doAction(String func, JSONObject json) {
        if(func == null) {
            return; // there is no func to pressed button
        }
        if(func.contentEquals("msg")) {
            sendMsg(json);
        } else if(func.contentEquals("call")) {
            callFriend();
        } else if(func.contentEquals("track")) {
            // TODO
        }
    }

    private void sendMsg(JSONObject json) {
        Log.e("MSG", "sendMsg " + json);
        SensorsInfo sensorsInfo = new SensorsInfo(json);
        MetaMsg metaMsg = Processor.getInstance().getMsgSettings(this);
        Person user = Processor.getInstance().getProtectedUser(this);
        ArrayList<Person> friends = Processor.getInstance().getCurrentFriendsList(this);
        final Msg msg = new Msg(user, sensorsInfo, metaMsg, friends);
        sendSocket(serverIp, serverPort, msg.toJson().toString());
    }

    public void callFriend() {
        String number = Processor.getInstance().getCallFriend(this);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.setData(Uri.parse("tel:" + number));
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    private static void sendSocket(final String ip, final int port, final String content) {
        Log.e("SOCKET", content);
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
