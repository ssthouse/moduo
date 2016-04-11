package com.ssthouse.moduo.fragment.moduo.presenter.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Canvas画图操作提取
 * Created by ssthouse on 2016/1/24.
 */
public class CanvasUtil {

    //从左上画出bitmap
    public static  void drawImage(Canvas canvas, Bitmap bitmap, int x, int y,
                           int w, int h) {
        Rect dst = new Rect();// 屏幕 >>目标矩形
        dst.left = x;
        dst.top = y;
        dst.right = x + w;
        dst.bottom = y + h;
        canvas.drawBitmap(bitmap, null, dst, null);
    }

    //从中心点画bitmap
    public static void drawBitmap(Canvas canvas, Bitmap bitmap, int centerX, int centerY, int width, int height) {
        Rect desRect = new Rect();
        desRect.left = centerX - width / 2;
        desRect.right = centerX + width / 2;
        desRect.top = centerY - height / 2;
        desRect.bottom = centerY + height / 2;
        canvas.drawBitmap(bitmap, null, desRect, null);
    }

}
