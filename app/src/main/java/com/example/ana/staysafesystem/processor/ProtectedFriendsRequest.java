package com.example.ana.staysafesystem.processor;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.ana.staysafesystem.data.Person;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ana on 03/12/17.
 */

public class ProtectedFriendsRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://staysafesystem.000webhostapp.com/protected.php";
    private Map<String, String> params;

    public ProtectedFriendsRequest(Person user,
                        Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("username", user.getName());
        params.put("phone", user.getPhoneNumber());
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
