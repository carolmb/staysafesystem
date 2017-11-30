package com.example.ana.staysafesystem.processor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {
    boolean connectSuccess = true;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    final int CONNECTION_RESULT_OK = 1;
    final int CONNECTION_RESULT_FAILED = -1;

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    BluetoothManager(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public Set<BluetoothDevice> getBondedDevices() {
        Set<BluetoothDevice> bondedSet = mBluetoothAdapter.getBondedDevices();
        return bondedSet;
    }

    public int connectToDevice(String deviceAddress) {
        if(btSocket != null){
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConnectBT bt = new ConnectBT(deviceAddress);
        bt.execute();
        return (connectSuccess) ? CONNECTION_RESULT_OK : CONNECTION_RESULT_FAILED;
    }

    public String getInfo() {
        String s = "";
        try {
            int c;

            while(btSocket.getInputStream().available() > 0){
                c = btSocket.getInputStream().read();
                s += (char)c;
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("could not get ppm");
        }

        return s;
    }

    class ConnectBT extends AsyncTask<Void, Void, Void> {
        String address = "";
        ConnectBT(String a){
            address = a;
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if(btSocket == null || !isBtConnected){
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                    btSocket.connect();
                }
            }
            catch (IOException e) {
                Log.e("erro aqui", "eitaaaa");
                e.printStackTrace();
                connectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!connectSuccess){
            }
            else{
                isBtConnected = true;
            }
        }
    }

}