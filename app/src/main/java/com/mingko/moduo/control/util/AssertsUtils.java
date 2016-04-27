package com.mingko.moduo.control.util;

import android.content.Context;

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
 * asset目录：../moduo/app/src/main/assets
 */
public class AssertsUtils {

    //TODO 也许以后会用的上
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
     * @param oriFilePath 源文件路径
     * @param desFilePath 目标文件路径
     * @return 操作完成返回 true， 否则将被捕捉错误
     */
    static public boolean copyFileTo(Context context, String oriFilePath, String desFilePath){
        InputStream myInput = null;
        OutputStream myOutput = null;
        byte[] buffer = new byte[1024];
        try {
            myInput = context.getAssets().open(oriFilePath);
            myOutput = new FileOutputStream(desFilePath);
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
        } catch (IOException e) {
            Timber.e("复制出错");
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
     * assert配置文件目录：../moduo/app/src/main/assets/Devices
     * app安装目录：data/data/com.mingko.moduo/files
     */
    public static boolean copyAllAssertToCacheFolder(Context context){
        String strDevices = "Devices";
        String devicesFolderPath = context.getFilesDir().toString() + File.separator + strDevices;
        File devicesFolder = new File(devicesFolderPath);
        //noinspection ResultOfMethodCallIgnored
        devicesFolder.mkdirs();
        String[] files = new String[0];
        try {
            files = context.getAssets().list(strDevices);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String file : files) {
            String oriFilePath = strDevices + File.separator + file;
            String desFilePath = devicesFolderPath + File.separator + file;
            File devFile = new File(desFilePath);
            if (!devFile.exists()) {
                copyFileTo(context, oriFilePath, desFilePath);
            }
        }
        return true;
    }
}
