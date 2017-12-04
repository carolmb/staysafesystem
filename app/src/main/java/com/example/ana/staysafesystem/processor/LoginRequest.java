package com.example.ana.staysafesystem.processor;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ana on 03/12/17.
 */

public class LoginRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://staysafesystem.000webhostapp.com/login.php";
    private Map<String, String> params;

    public LoginRequest(String username, String phone,
                        Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        params.put("username", username);
        params.put("phone", phone);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
