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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件帮助类{
 *     管理魔哆在sd卡中的文件
 *     管理视频对话生成的照片和视频
 * }
 * Created by ssthouse on 2015/12/28.
 */
public class FileUtil {

    /**
     * 程序在SD卡的跟路径
     */
    public static final String MODUO_SDCARD_FOLDER_PATH = SDCardUtil.getSDCardPath() + "Moduo" + File.separator;

    //魔哆二维码照片path
    public static final String MODUO_QRCODE_PATH = MODUO_SDCARD_FOLDER_PATH + "Qrcode" + File.separator;

    //魔哆视频通话---截图path---录像path
    public static final String MODUO_VIDEO_PIC_PATH = MODUO_SDCARD_FOLDER_PATH + "Picture" + File.separator;
    public static final String MODUO_VIDEO_VIDEO_PATH = MODUO_SDCARD_FOLDER_PATH + "Video" + File.separator;

    //语音聊天path
    public static final String MODUO_TALK_PATH = MODUO_SDCARD_FOLDER_PATH + "Talk" + File.separator;

    /**
     * 初始化魔哆sd卡路径   以及必要的文件路径
     */
    public static void initModuoFolder() {
        //二维码路径
        File qrCodeFile = new File(MODUO_QRCODE_PATH);
        if (!qrCodeFile.exists()) {
            qrCodeFile.mkdirs();
        }
        //视频通话的  截图、录像 路径
        File picFile = new File(MODUO_VIDEO_PIC_PATH);
        if (!picFile.exists()) {
            picFile.mkdirs();
        }
        File videoFile = new File(MODUO_VIDEO_VIDEO_PATH);
        if (!videoFile.exists()) {
            videoFile.mkdirs();
        }
        //语音通话记录path
        File talkFile = new File(MODUO_TALK_PATH);
        if (!talkFile.exists()) {
            talkFile.mkdirs();
        }
    }

    /**
     * 保存bitmap到本地:
     * 因为一台设备的分享二维码是不会改变的, 所以did作为唯一识别
     *
     * @param bitmap
     * @param did     以设备号为二维码名称
     * @return 成功返回路径---不成功返回空
     */
    public static String saveBitmap(Bitmap bitmap, String did) {
        String filePath = MODUO_QRCODE_PATH + did + ".png";
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
        File picFile = new File(MODUO_QRCODE_PATH + picName);
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

    /**
     * 生成新的视频通话截图文件路径
     *
     * @return 新生成的文件路径
     */
    public static String generateNewPicFilePath() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh:mm:ss");
        String fileName = dateFormat.format(date);
        return MODUO_VIDEO_PIC_PATH + fileName + ".jpg";
    }

    /**
     * 生成新的视频通话录像文件path
     *
     * @return 新的录像文件路径
     */
    public static String generateNewVideoFilePath() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh:mm:ss");
        String fileName = dateFormat.format(date);
        return MODUO_VIDEO_VIDEO_PATH + fileName + ".mp4";
    }
}
