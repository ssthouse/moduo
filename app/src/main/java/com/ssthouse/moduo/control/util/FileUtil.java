package com.ssthouse.moduo.control.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 文件帮助类
 * Created by ssthouse on 2015/12/28.
 */
public class FileUtil {

    /**
     * 程序在SD卡的跟路径
     */
    public static final String MODUO_SDCARD_FOLDER_PATH = SDCardUtil.getSDCardPath() + "Moduo" + File.separator;

    //魔哆二维码照片path
    public static final String MODUO_PICTURE_PATH = MODUO_SDCARD_FOLDER_PATH + "pic" + File.separator;

    /**
     * 初始化魔哆sd卡路径
     */
    public static void initModuoFolder() {
        File picFile = new File(MODUO_PICTURE_PATH);
        if (!picFile.exists()) {
            picFile.mkdirs();
        }
    }

    /**
     * 保存bitmap到本地:
     * 因为一台设备的分享二维码是不会改变的, 所以did作为唯一识别
     *
     * @param context
     * @param bitmap
     * @param did     以设备号为二维码名称
     * @return 成功返回路径---不成功返回空
     */
    public static String saveBitmap(Context context, Bitmap bitmap, String did) {
        String filePath = MODUO_PICTURE_PATH + did + ".png";
        try {
            OutputStream os = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
            //flush
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePath;
    }

    /**
     * 是否已生成图片文件
     *
     * @param picName
     * @return
     */
    public static boolean hasPicture(String picName) {
        File picFile = new File(MODUO_PICTURE_PATH + picName);
        if (picFile.exists()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 调用系统分享文件
     *
     * @param context
     * @param file
     */
    public static void sharePicture(Context context, File file) {
        //使用intent分享照片
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri uri = Uri.fromFile(file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        //开启分享
        context.startActivity(shareIntent);
    }

}
