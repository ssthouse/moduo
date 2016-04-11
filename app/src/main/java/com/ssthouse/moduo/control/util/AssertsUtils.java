package com.ssthouse.moduo.control.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * asset操作工具类
 */
public class AssertsUtils {

    static public String getTextByName(Context c, String name) {
        String result = "";
        try {
            InputStream in = c.getResources().getAssets().open(name);
            BufferedReader brReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = brReader.readLine()) != null) {
                result += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从assert中复制出文件到某个文件
     *
     * @param c
     * @param oriFile
     * @param desFile
     * @return
     * @throws IOException
     */
    static public boolean copyFileTo(Context c, String oriFile, String desFile) throws IOException {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(desFile);
        myInput = c.getAssets().open(oriFile);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();

        return true;
    }

    /**
     * 复制assert中的配置文件到app安装目录
     */
    static public boolean copyAllAssertToCacheFolder(Context c) throws IOException {
        String[] files = c.getAssets().list("Devices");
        String fileFolder = c.getFilesDir().toString();
        File deviceFile = new File(fileFolder + "/Devices/");
        deviceFile.mkdirs();
        for (int i = 0; i < files.length; i++) {
            File devfile = new File(fileFolder + "/Devices/" + files[i]);
            if (!devfile.exists()) {
                copyFileTo(c, "Devices/" + files[i], fileFolder + "/Devices/" + files[i]);
            }
        }
        String[] fileStr = deviceFile.list();
        for (int i = 0; i < fileStr.length; i++) {
            Log.i("file", fileStr[i]);
        }
        return true;
    }
}
