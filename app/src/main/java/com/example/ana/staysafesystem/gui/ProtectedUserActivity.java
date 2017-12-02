package com.example.ana.staysafesystem.gui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.Processor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.aflak.bluetooth.Bluetooth;

public class ProtectedUserActivity extends AppCompatActivity implements Bluetooth.CommunicationCallback {

    static TextView bluetoothText;
    private Bluetooth b;
    private boolean registered = false;

    @Override
    public void onConnect(BluetoothDevice device) {
        Display("Connected to "+device.getName()+" - "+device.getAddress());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //send.setEnabled(true);
            }
        });
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        Display("Disconnected!");
        Display("Connecting again...");
        b.connectToDevice(device);
    }

    @Override
    public void onMessage(String message) {
        Display(message);
    }

    @Override
    public void onError(String message) {
        Display("Error: "+message);
    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Display("Error: "+message);
        Display("Trying again in 3 sec.");
        runOnUiThread(new Runnable() {
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
    }

    public void Display(final String s){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bluetoothText.setText(s);
                //scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    class FuncButton {
        TextView description;
        ImageButton img;
    }
    public static final int RequestPermissionCode = 1;
    ArrayList<FuncButton> funcButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protected_user);
        setTitle("3S - Protegido");
        bluetoothText = findViewById(R.id.bluetoothMsg);

        b = new Bluetooth(this);
        b.enableBluetooth();

        b.setCommunicationCallback(this);

        int pos = getIntent().getExtras().getInt("pos");

        Display("Connecting...");
        b.connectToDevice(b.getPairedDevices().get(pos));

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered = true;

        setFuncButtons();

        String func1 = Processor.getInstance().getButtonFunc(this, 1);
        configButton(func1, 1);

        String func2 = Processor.getInstance().getButtonFunc(this, 2);
        configButton(func2, 2);

        saveMeButtons();
    }

    private void setFuncButtons() {
        funcButtons = new ArrayList<>();

        FuncButton funcButton1 = new FuncButton();
        funcButton1.description = findViewById(R.id.func1);
        funcButton1.img = findViewById(R.id.imageButton1);
        funcButtons.add(funcButton1);

        FuncButton funcButton2 = new FuncButton();
        funcButton2.description = findViewById(R.id.func2);
        funcButton2.img = findViewById(R.id.imageButton2);
        funcButtons.add(funcButton2);
    }

    void configButton(String func, int id) {
        if(func != null) {
            showFunc(func, id);
        } else {
            nullFunc(id);
        }
    }

    void nullFunc(final int id) {
        TextView description = funcButtons.get(id-1).description;
        description.setText("Não configurado");

        ImageButton imageButton = funcButtons.get(id-1).img;
        imageButton.setImageResource(android.R.drawable.ic_input_add);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddFuncActivity.class);
                intent.putExtra("buttonPressed", id);
                view.getContext().startActivity(intent);
            }
        });
    }

    void showFunc(String func, final int id) {
        TextView description = funcButtons.get(id-1).description;
        description.setText(getPrettyName(func));

        ImageButton imageButton = funcButtons.get(id-1).img;
        imageButton.setImageResource(android.R.drawable.ic_menu_delete);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Processor.getInstance().setButtonFunc(view.getContext(), id, null);
                nullFunc(id);
            }
        });
    }

    private String getPrettyName(String funcCode) {
        String prettyName = null;
        if(funcCode.contentEquals("call")) {
            prettyName = "Ligar para contato especial";
        } else if(funcCode.contentEquals("track")) {
            prettyName = "Rastrear aparelho";
        } else if(funcCode.contentEquals("msg")) {
            prettyName = "Mensagem para amigos";
        }
        return prettyName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuAbout:
                UtilGUI.dialog(this,
                        "Stay Safe System foi pensado com carinho para te ajudar.");
                break;

            case R.id.menuProfile:
                UtilGUI.changeScreen(this, ProfileActivity.class);
                break;

            case R.id.menuMode:
                // clean mode
                Processor.getInstance().clearMode(this);
                UtilGUI.changeScreen(this, UserModeActivity.class);
                break;

            case R.id.menuLogout:
                // logout + clean mode
                Processor.getInstance().logout(this);
                UtilGUI.changeScreen(this, LoginActivity.class);
                break;

        }
        return true;
    }

    void saveMeButtons() {
        ImageButton helpMe1 = findViewById(R.id.helpMe);
        helpMe1.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                UtilGUI.dialog(view.getContext(), "Deu certo 1.");
                JSONObject json = new JSONObject();
                try {
                    json.put("heart", 0);
                    json.put("local", "bla");
                    json.put("buttonId", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Processor.getInstance().buttonPressed(view.getContext(), json);
            }
        });

        ImageButton helpMe2 = findViewById(R.id.helpMe11);
        helpMe2.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                UtilGUI.dialog(view.getContext(), "Deu certo 2.");
                JSONObject json = new JSONObject();
                try {
                    json.put("heart", 0);
                    json.put("local", "bla");
                    json.put("buttonId", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Processor.getInstance().buttonPressed(view.getContext(), json);
            }
        });
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);
            bluetoothText.setText(dataString);
        }
    };

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (!(PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED)) {
                    UtilGUI.dialog(this, "Esse aplicativo não tem permissão para " +
                            "pegar seu uddi.");
                }
                break;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Intent intent1 = new Intent(ProtectedUserActivity.this, SelectPairActivity.class);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                }
            }
        }
    };
}
