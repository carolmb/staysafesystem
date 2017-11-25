package com.example.ana.staysafesystem.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ana on 24/11/17.
 */

public class SensorsInfo {
    int heart;
    String location;

    public SensorsInfo(int heart, String location) {
        this.heart = heart;
        this.location = location;
    }

    /*
    * { "heart":0, "local":"", "fall":false }
    * */
    public SensorsInfo(JSONObject json) {
        try {
            heart = json.getInt("heart");
            location = json.getString("local");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toString(MetaMsg metaMsg) {
        String msg = "";
        if(metaMsg.heartbeat) {
            msg = "Batimento Cardíaco: " + heart + "\n";
        }
        if(metaMsg.local) {
            msg += "Localização: " + location + "\n";
        }
        return msg;
    }

}
