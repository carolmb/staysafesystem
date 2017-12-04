package com.example.ana.staysafesystem.processor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.ana.staysafesystem.data.Call;
import com.example.ana.staysafesystem.data.DataInternalStorage;
import com.example.ana.staysafesystem.data.MetaMsg;
import com.example.ana.staysafesystem.data.Person;
import com.example.ana.staysafesystem.gui.UserModeActivity;
import com.example.ana.staysafesystem.gui.UtilGUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ana on 19/11/17.
 */
public class Processor {

    DataInternalStorage<ArrayList<Person>> internalStorageFriends;
    DataInternalStorage<MetaMsg> internalStorageMsgSettings;
    ArrayList<Person> friendsListCache;
    ArrayList<Call> callsCache;
    ArrayList<Person> protectedFriendsCache;

    static private Processor instance;
    private Processor() {
        internalStorageFriends = new DataInternalStorage<>("friendsList");
        internalStorageMsgSettings = new DataInternalStorage<>("msgSettings");
    }

    static public Processor getInstance() {
        if(instance != null) {
            return instance;
        } else {
            instance = new Processor();
            return instance;
        }
    }

    public void initInternalMemory(Context context) {
        Log.e("aaaaaaa", "aaaaaaaaaa");
        Person user = getProtectedUser(context);
        ServerConnectionService.getCalls(user);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("PROTECTED", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success) {
                        ArrayList<Person> protectedFriends = new ArrayList<>();
                        JSONArray array = jsonResponse.getJSONArray("protected");
                        for(int i = 0; i < array.length(); i++) {
                            protectedFriends.add(new Person(array.getJSONObject(i)));
                        }
                        protectedFriendsCache = protectedFriends;
                    } else {
                        Log.e("PROTECTED", "Erro ao solicitar protected friends");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("VOLLEY", volleyError.getMessage());
                volleyError.printStackTrace();
            }
        };
        ProtectedFriendsRequest protectedFriendsRequest =
                new ProtectedFriendsRequest(user, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(protectedFriendsRequest);
    }

    public void updateCallsCache(ArrayList<Call> calls) {
        callsCache = calls;
    }

    public ArrayList<Person> getProtectedFriends() {
        return protectedFriendsCache;
    }

    public ArrayList<Person> getCurrentFriendsList(Context context) {
        if(friendsListCache == null) {
            friendsListCache = internalStorageFriends.getObj(context);
        }
        return friendsListCache;
    }

    public void updateFriendInList(Context context, Person person) {
        if(friendsListCache == null) {
            friendsListCache = internalStorageFriends.getObj(context);
        }
        if(!friendsListCache.contains(person)) {
            friendsListCache.add(person);
        } else {
            friendsListCache.remove(person);
        }
    }

    public boolean isInCacheFriendsList(Person person) {
        return friendsListCache.contains(person);
    }

    public void saveCacheFriendsList(final Context context, ArrayList<Person> guardians) {
        internalStorageFriends.saveObj(context, friendsListCache);
        Person user = getProtectedUser(context);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success) {
                        UtilGUI.dialog(context, "Lista salva com sucesso.");
                    } else {
                        UtilGUI.dialog(context, "Ocorreu um problema.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("VOLLEY", volleyError.getMessage());
                volleyError.printStackTrace();
            }
        };
        GuardiansListRequest guardiansListRequest =
                new GuardiansListRequest(user, guardians, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(guardiansListRequest);
    }

    public void setMsgSettings(Context context, MetaMsg msgSettings) {
        internalStorageMsgSettings.saveObj(context, msgSettings);
    }

    public MetaMsg getMsgSettings(Context context) {
        return internalStorageMsgSettings.getObj(context);
    }

    public void clearMode(Context context) {
        setPref(context, "mode", "mode", null);
    }

    public void logout(Context context) {
        setProtectedUser(context, new Person(null, null));
        setUserMode(context, null);
        setButtonFunc(context, 1, null);
        setButtonFunc(context, 2, null);
        internalStorageFriends.saveObj(context, new ArrayList<Person>());
    }

    public String getButtonFunc(Context context, int id) {
        return getPref(context, "button", "b" + id);
    }

    public void setButtonFunc(Context context, int id, String value) {
        setPref(context, "button", "b" + id, value);
    }

    public String getUserMode(Context context) {
        return getPref(context, "mode", "mode");
    }

    public void setUserMode(Context context, String mode){
        setPref(context, "mode", "mode", mode);
    }

    public Person getProtectedUser(Context context) {
        String userName = getPref(context, "login", "userName");
        String userNumber = getPref(context, "login", "userNumber");
        return new Person(userName, userNumber);
    }

    public void setProtectedUser(final Context context, final Person person) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("LOGIN", response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success) {
                        setPref(context, "login", "userName", person.getName());
                        setPref(context, "login", "userNumber", person.getPhoneNumber());
                        Intent intent = new Intent(context, UserModeActivity.class);
                        context.startActivity(intent);
                    } else {
                        UtilGUI.dialog(context, "Login não ocorreu com sucesso, tente novamente.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("VOLLEY", volleyError.getMessage());
                volleyError.printStackTrace();
            }
        };
        LoginRequest loginRequest =
                new LoginRequest(person.getName(), person.getPhoneNumber(), responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(loginRequest);
    }

    public boolean isValidUser(Context context) {
        Person protectedUser = getProtectedUser(context);
        if(protectedUser.isValid()) {
            return true;
        }
        return false;
    }

    public void setCallFriend(Context context, String phoneNumber, String audioType) {
        setPref(context, "callFriend", "friendPhoneNumber", phoneNumber);
        setPref(context, "callFriend", "isRecorded", audioType);
    }

    public String getCallFriend(Context context) {
        String number = getPref(context, "callFriend", "friendPhoneNumber");
        return number;
    }

    public void setEmail(Context context, String email){
        setPref(context, "emailTracking", "email", email);
    }

    private void setPref(Context context, String pref, String field, String value) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(pref, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(field, value);
        ed.commit();
    }

    private String getPref(Context context, String pref, String field) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(pref, Context.MODE_PRIVATE);
        return sharedPref.getString(field, null);
    }

}
