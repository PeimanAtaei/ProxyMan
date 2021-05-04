package com.misoke.proxyman.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by mohammad on 3/19/18.
 */

public class FileHelper {

    private Context context;
    private File file;

    private static final String DIR_APP = "/proxyman/";

    public FileHelper(Context context) {
        this.context = context;
        file = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + DIR_APP);
    }

    public void initialize() {
        file.mkdirs();
        //Log.i("LOG", "initialize");
    }

    public String getFilePath(String name) {
        return file.getAbsoluteFile() + name;
    }
}
