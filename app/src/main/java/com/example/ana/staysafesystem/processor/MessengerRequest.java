package com.example.ana.staysafesystem.processor;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.ana.staysafesystem.data.Msg;
import com.example.ana.staysafesystem.data.Person;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ana on 03/12/17.
 */

public class MessengerRequest extends StringRequest {

    private static final String MSG_REQUEST_URL = "https://staysafesystem.000webhostapp.com/msg.php";
    private Map<String, String> params;

    public MessengerRequest(Msg msg,
                            Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, MSG_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        Person user = msg.getFrom();
        params.put("username", user.getName());
        params.put("phone", user.getPhoneNumber());
        params.put("msg", msg.toJson().toString());
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

