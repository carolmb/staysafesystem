package com.example.ana.staysafesystem.gui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ana.staysafesystem.R;
import com.example.ana.staysafesystem.processor.BluetoothManager;
import com.example.ana.staysafesystem.processor.Processor;

import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothManager btManager = Processor.btManager;
    final int BLUETOOTH_ENABLED_REQUEST_CODE = 1;
    ArrayAdapter devicesAdapter;
    String deviceInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Conexão Bluetooth");
        setContentView(R.layout.activity_bluetooth);
        devicesAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        startBluetooth();
    }

    private void startBluetooth() {
        mBluetoothAdapter = btManager.getmBluetoothAdapter();

        if (!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_ENABLED_REQUEST_CODE);
        }

        if (mBluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Não é compatível.")
                    .setMessage("Seu celular não suporta Bluetooth.")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        findDevice();
    }

    private void findDevice() {
        listPairedDevices();
    }

    private void listPairedDevices() {
        final ListView deviceList = (ListView) findViewById(R.id.devicesList);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        Set<BluetoothDevice> pairedDevices  = btManager.getBondedDevices();

        if (pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                devicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            deviceList.setAdapter(devicesAdapter);
            deviceList.setOnItemClickListener(myListClickListener);
        }
        else{
            alertDialog.setMessage("No devices found paired");
            alertDialog.show();
        }
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            deviceInfo = ((TextView) view).getText().toString();
            String deviceAddress = deviceInfo.substring(deviceInfo.length() - 17);
            int result = btManager.connectToDevice(deviceAddress);
            if(result == 1){
                Toast.makeText(BluetoothActivity.this, "Conectado a " + deviceInfo, Toast.LENGTH_SHORT).show();
                Processor.isConnected = true;
                UtilGUI.changeScreen(view.getContext(), ProtectedUserActivity.class);
            }
            else{
                Toast.makeText(BluetoothActivity.this, "Falha ao se conectar.", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_ENABLED_REQUEST_CODE){
            if(resultCode == RESULT_OK){

            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Habilite uso do Bluetooth.", Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        }
    }

}