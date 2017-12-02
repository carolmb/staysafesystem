package com.example.ana.staysafesystem.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.ana.staysafesystem.gui.ProtectedUserActivity;
import com.example.ana.staysafesystem.processor.BluetoothService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ana on 24/11/17.
 */

public class SensorsInfo {
    int heart;
    String local;

    Context context;

    /*
    * { "heart":0, "local":"", "fall":false }
    * */
    public SensorsInfo(JSONObject json) {
        try {
            heart = json.getInt("heart");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(BluetoothService.location != null) {
            this.local = "Latitude: " + BluetoothService.location.getLatitude() + " " +
                    "Longetude: " + BluetoothService.location.getLongitude();
        } else {
            this.local = "Local indisponível";
            Log.e("LOCAL", "problema com localização");
        }
    }

    public String toString(MetaMsg metaMsg) {
        String msg = "";
        if (metaMsg.heartbeat) {
            msg = "Batimento Cardíaco: " + heart + "\n";
        }
        if (metaMsg.local) {
            msg += "Localização: " + local + "\n";
            Log.e("LOCAL", local);
        }
        return msg;
    }

}
