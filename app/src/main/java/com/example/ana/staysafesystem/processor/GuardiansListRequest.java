package com.example.ana.staysafesystem.processor;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.ana.staysafesystem.data.Person;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ana on 03/12/17.
 */

public class GuardiansListRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "https://staysafesystem.000webhostapp.com/guardians.php";
    private Map<String, String> params;

    public GuardiansListRequest(Person user, ArrayList<Person> guardians,
                        Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener);
        params = new HashMap<>();
        JSONArray array = new JSONArray();
        for(Person p: guardians) {
            array.put(p.toJson());
        }
        params.put("username", user.getName());
        params.put("phone", user.getPhoneNumber());
        params.put("guardians", array.toString());
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
