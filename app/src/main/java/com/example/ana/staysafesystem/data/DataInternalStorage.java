package com.example.ana.staysafesystem.data;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.sun.jna.platform.win32.Netapi32Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by ana on 20/11/17.
 */

public class DataInternalStorage<T> {

    String LOCAL;

    public DataInternalStorage(String local) {
        LOCAL = local;
    }

    private static File mFolder;
    public void saveObj(Context context, T obj) {
        //this could be initialized once onstart up
        if(mFolder == null){
            mFolder = context.getExternalFilesDir(null);
        }
        ObjectOutput out;
        try {
            File outFile = new File(mFolder,
                    LOCAL + ".data");
            out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(obj);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T getObj(Context context) {
        if(mFolder == null){
            mFolder = context.getExternalFilesDir(null);
        }
        ObjectInput in;
        T obj = null;
        try {
            FileInputStream fileIn = new FileInputStream(
                    mFolder.getPath() + File.separator + LOCAL + ".data");
            in = new ObjectInputStream(fileIn);
            obj = (T) in.readObject();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
