package com.example.ana.staysafesystem.gui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

/**
 * Created by ana on 19/11/17.
 */

public class Util {
    public static void dialog(String msg, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void changeScreen(Context context, Class<?> cls) {
        Intent myIntent = new Intent(context, cls);
        context.startActivity(myIntent);
    }

    public static void setPref(Context context, String pref, String field, String value) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(pref, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(field, value);
        ed.commit();
    }

    public static String getPref(Context context, String pref, String field) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(pref, Context.MODE_PRIVATE);
        return sharedPref.getString(field, null);
    }
}
