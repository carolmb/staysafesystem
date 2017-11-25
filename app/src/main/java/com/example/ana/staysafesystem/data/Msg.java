package com.example.ana.staysafesystem.data;

import com.example.ana.staysafesystem.processor.ACTION;
import com.example.ana.staysafesystem.processor.Processor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ana on 24/11/17.
 */

public class Msg {
    String title;
    String subtitle;
    String content;
    Person from;
    ArrayList<Person> friends;

    public Msg(Person from, String title, String subtitle, String content) {
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.from = from;
    }

    public Msg(Person person, SensorsInfo sensorsInfo, MetaMsg metaMsg, ArrayList<Person> friends) {
        title = person.getName() + " está pedindo ajuda!";
        subtitle = "Seu amigo " + person.getName() + " está pedindo ajuda, faça algo por ele!";
        content = metaMsg.content + "\n" + sensorsInfo.toString(metaMsg);
        this.friends = friends;
        this.from = person;
    }

    public Msg(JSONObject msg) {
        try {
            this.title = msg.getString("title");
            this.subtitle = msg.getString("subtitle");
            this.content = msg.getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("action", ACTION.MSG);
            json.put("title", title);
            json.put("subtitle", subtitle);
            json.put("content", content);
            json.put("name", from.getName());
            json.put("phone", from.getPhoneNumber());
            JSONArray jsonArray = new JSONArray();
            for (Person f: friends) {
                JSONObject fJson = f.toJson();
                jsonArray.put(fJson);
            }
            json.put("friends", jsonArray);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getContent() {
        return content;
    }
}
