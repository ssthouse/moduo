package com.ssthouse.moduo.main.control.util;

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
 * 二维码工具类
 * Created by ssthouse on 2015/12/19.
 */
public class QrCodeUtil {

    /**
     * 二维码content前缀
     */
    private static final String qrCodePrefix = "http://site.gizwits.com/?";

    /**
     * 获取设备分享二维码url
     *
     * @param productKey
     * @param did
     * @param passcode
     * @param cid
     * @param username
     * @param password
     * @return
     */
    public static String getDeviceQrCodeContent(String productKey, String did, String passcode,
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
     * 获取wifi分享二维码url
     *
     * @param wifiSsid
     * @param password
     * @return
     */
    public static String getWifiQrCodeContent(String wifiSsid, String password) {
        String content = qrCodePrefix +
                "wifi_ssid=" + wifiSsid +
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
     * 在URL中获取key对应value
     * 用于解析URL中想要的数据
     *
     * @param url
     * @param key
     * @return
     */
    public static String getParamFromUrl(String url, String key) {
        String value;
        int startIndex = url.indexOf(key + "=");
        //如果二维码没有指定的参数---返回null
        if (startIndex == -1) {
            return null;
        }
        startIndex += (key.length() + 1);
        String subString = url.substring(startIndex);
        int endIndex = subString.indexOf("&");
        if (endIndex == -1) {
            value = subString;
        } else {
            value = subString.substring(0, endIndex);
        }
        return value;
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
