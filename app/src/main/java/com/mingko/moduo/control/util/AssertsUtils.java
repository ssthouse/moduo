package com.mingko.moduo.control.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import timber.log.Timber;

/**
 * asset操作工具类
 */
public class AssertsUtils {

    static public String getTextByName(Context context, String name) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(name);
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
     * @param context context
     * @param oriFile 源文件
     * @param desFile 目标文件
     * @return 操作完成返回 true， 否则将被捕捉错误
     */
    static public boolean copyFileTo(Context context, String oriFile, String desFile){
        InputStream myInput = null;
        OutputStream myOutput = null;
        byte[] buffer = new byte[1024];
        try {
            myOutput = new FileOutputStream(desFile);
            myInput = context.getAssets().open(oriFile);
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
        } catch (IOException e) {
            Timber.e("AssertsUtils 复制出错");
            e.printStackTrace();
        } finally {
            try {
                if(myOutput != null){
                    myOutput.flush();
                    myOutput.close();
                }
                if(myInput != null){
                    myInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 复制assert中的配置文件到app安装目录
     */
    public static boolean copyAllAssertToCacheFolder(Context context){
        String[] files = new String[0];
        try {
            files = context.getAssets().list("Devices");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileFolder = context.getFilesDir().toString();
        File deviceFile = new File(fileFolder + "/Devices/");
        //noinspection ResultOfMethodCallIgnored
        deviceFile.mkdirs();
        for (String file : files) {
            File devfile = new File(fileFolder + "/Devices/" + file);
            if (!devfile.exists()) {
                copyFileTo(context, "Devices/" + file, fileFolder + "/Devices/" + file);
            }
        }
        String[] fileStr = deviceFile.list();
        for (String aFileStr : fileStr) {
            Log.i("file", aFileStr);
        }
        return true;
    }
}
