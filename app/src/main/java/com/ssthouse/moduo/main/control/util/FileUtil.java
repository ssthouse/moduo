package com.ssthouse.moduo.main.control.util;

import android.content.Context;
import android.graphics.Bitmap;

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
     * 保存bitmap到本地
     *
     * @param bitmap
     * @return 成功返回路径---不成功返回空
     */
    public static String saveBitmap(Context context, Bitmap bitmap) {
        String filePath = SDCardUtil.getSDCardPath() + System.currentTimeMillis();
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
}
