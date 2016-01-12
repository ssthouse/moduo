package com.ssthouse.moduo.control.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ssthouse.moduo.main.view.activity.ScanActivity;

/**
 * 调用二维码扫描功能
 * Created by ssthouse on 2015/12/19.
 */
public class ScanUtil {

    /**
     * 二维码content前缀
     */
    private static final String qrCodePrefix = "http://site.gizwits.com/?";

    /**
     * 获取给定参数的二维码的content
     *
     * @param productKey
     * @param did
     * @param passcode
     * @param cid
     * @param username
     * @param password
     * @return
     */
    public static String getQrCodeContent(String productKey, String did, String passcode,
                                          String cid, String username, String password) {
        String content = qrCodePrefix +
                "product_key=" + productKey +
                "&did=" + did +
                "&passcode=" + passcode +
                "&cid=" + cid +
                "&username=" + username +
                "&password=" + password;
        return content;
    }


    /**
     * 开启扫描activity
     */
    public static void startScan(Context context) {
        IntentIntegrator integrator = new IntentIntegrator((Activity) context);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    /**
     * 在URL中获取参数值
     * 用于解析URL中想要的数据
     *
     * @param url
     * @param param
     * @return
     */
    public static String getParamFromUrl(String url, String param) {
        String product_key;
        int startIndex = url.indexOf(param + "=");
        //如果二维码没有指定的参数---返回null
        if (startIndex == -1) {
            return null;
        }
        startIndex += (param.length() + 1);
        String subString = url.substring(startIndex);
        int endIndex = subString.indexOf("&");
        if (endIndex == -1) {
            product_key = subString;
        } else {
            product_key = subString.substring(0, endIndex);
        }
        return product_key;
    }


    /**
     * 二维矩阵转bitmap
     *
     * @param matrix
     * @return
     */
    public static Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] rawData = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = Color.WHITE;
                if (matrix.get(i, j)) {
                    color = Color.BLACK;
                }
                rawData[i + (j * w)] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }

    /**
     * 生成二维码
     *
     * @param content
     * @return
     */
    public static Bitmap generateQRCode(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            // MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 500, 500);
            return bitMatrix2Bitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
