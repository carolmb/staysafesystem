package com.example.ana.staysafesystem.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ana on 22/11/17.
 */

public class Call {
    String day;
    String hour;
    String description;
    String finished;

    public Call(String day, String hour, String description, String finished) {
        this.day = day;
        this.hour = hour;
        this.description = description;
        this.finished = finished;
    }

    public Call(String day, String hour, String description) {
        this.day = day;
        this.hour = hour;
        this.description = description;
        this.finished = "Não foi resolvido";
    }

    public Call(JSONObject call) {
        try {
            JSONObject msg = call.getJSONObject("msg");
            String from = call.getString("from");
            boolean status = call.getBoolean("status");
            this.description = new Msg(msg).toString();
            this.finished = status ? "Foi resolvido" : "Não foi resolvido" ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setFinished() {
        finished = "Foi resolvido";
    }

    public String longToString() {
        return day + "\n" + hour + "\n" + description + '\n' + finished;
    }

    public String toString() {
        return day + " " + hour;
    }
}
