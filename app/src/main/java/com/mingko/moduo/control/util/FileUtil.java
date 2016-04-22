package com.mingko.moduo.control.util;

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
import java.util.Locale;
import java.util.UUID;

/**
 * 文件帮助类{
 *     管理魔哆在sd卡中的文件
 *     管理视频对话生成的照片和视频
 * }
 * Created by ssthouse on 2015/12/28.
 */
public class FileUtil {

    /**
     * 程序在SD卡的根路径
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
        File qrCodePath = new File(MODUO_QRCODE_PATH);
        createDirectory(qrCodePath);
        //视频通话的  截图路径
        File picPath = new File(MODUO_VIDEO_PIC_PATH);
        createDirectory(picPath);
        //视频通话的  录像路径
        File videoPath = new File(MODUO_VIDEO_VIDEO_PATH);
        createDirectory(videoPath);
        //语音通话记录path
        File talkPath = new File(MODUO_TALK_PATH);
        createDirectory(talkPath);
    }

    /**
     * 初始化文件夹
     *
     * @param dirPath 文件目录
     */
    private static void createDirectory(File dirPath){
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
    }

    /**
     * 保存魔哆二维码到本地:
     * 因为一台设备的分享二维码是不会改变的, 所以did作为唯一识别
     *
     * @param bitmap 二维码图片
     * @param did device id 设备唯一识别号
     * @return 操作成功返回二维码图片路径，否则返回空
     */
    public static String saveBitmap(Bitmap bitmap, String did) {
        String filePath = MODUO_QRCODE_PATH + did + ".png";
        try {
            OutputStream os = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
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
     * @param picName 图片名
     * @return 图片存在返回true ，否则返回false
     */
    public static boolean hasPicture(String picName) {
        File picFile = new File(MODUO_QRCODE_PATH + picName);
        return picFile.exists();
    }

    /**
     * 调用系统分享文件
     *
     * @param context context
     * @param picFile 需要分享的图片文件
     */
    public static void sharePicture(Context context, File picFile) {
        //使用intent分享照片
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri uri = Uri.fromFile(picFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        //开启分享
        context.startActivity(shareIntent);
    }

    /**
     * 生成新的视频通话截图文件路径
     * 文件路径：图片路径\截图时间.jpg
     *
     * @return 新生成的图片文件路径
     */
    public static String generateNewPicFilePath() {

        return MODUO_VIDEO_PIC_PATH + generateUniqueFileName() + ".jpg";
    }

    /**
     * 生成新的视频通话录像文件path
     *
     * @return 新的录像文件路径
     */
    public static String generateNewVideoFilePath() {
        return MODUO_VIDEO_VIDEO_PATH + generateUniqueFileName() + ".mp4";
    }

    /**
     * 生成唯一文件名
     * 文件名由 时间+前8位的UUID序列号作为唯一识别
     * 关于UUID序列号，可继续优化
     *
     * @return 唯一识别文件名
     */
    private static String generateUniqueFileName(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh:mm:ss", Locale.getDefault());
        String fileName = dateFormat.format(date);
        fileName += UUID.randomUUID().toString().substring(0,8);
        return fileName;
    }
}
