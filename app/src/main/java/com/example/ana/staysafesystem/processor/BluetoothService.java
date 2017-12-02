package com.example.ana.staysafesystem.processor;

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
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.data.Msg;
import com.example.ana.staysafesystem.data.Person;
import com.example.ana.staysafesystem.gui.FriendAskingHelpActivity;
import com.example.ana.staysafesystem.gui.ProtectedUserActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ana on 01/12/17.
 */

public class BluetoothService extends Service implements Bluetooth.CommunicationCallback {

    static public Bluetooth b;
    static boolean registered = false;

    private NotificationManager mNM;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int id) {
        if(!registered){
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

    @Override
    public void onMessage(final String message) {
        Intent intent = new Intent(this, ServerConnectionService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.e("EITAAA", componentName.getClassName());
                ServerConnectionService myServiceBinder;
                myServiceBinder = ((ServerConnectionService.Binder) iBinder).getService();
                try {
                    myServiceBinder.buttonPressed(new JSONObject(message));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(intent, serviceConnection, BIND_IMPORTANT);
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
}
