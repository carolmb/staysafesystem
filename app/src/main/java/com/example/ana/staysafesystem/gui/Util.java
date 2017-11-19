package com.example.ana.staysafesystem.gui;

import android.content.Context;
import android.content.Intent;
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

}
