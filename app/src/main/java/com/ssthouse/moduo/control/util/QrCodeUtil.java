package com.ssthouse.moduo.control.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ssthouse.moduo.activity.ScanActivity;
import com.ssthouse.moduo.model.cons.ScanCons;
import com.ssthouse.moduo.model.event.scan.ScanDeviceEvent;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 二维码工具类{
 *     解析二维码中string数据
 *     生成二维码string数据
 *     生成二维码bitmap
 * }
 * Created by ssthouse on 2015/12/19.
 */
public class QrCodeUtil {

    /**
     * 二维码content前缀
     */
    private static final String QR_CODE_PREFIX = "http://site.gizwits.com/?";

    /**
     * 解析扫描出的魔哆设备数据
     *
     * @param context
     * @param intentResult 魔哆二维码数据
     */
    public static void parseScanResult(Context context, IntentResult intentResult) {
        if (intentResult == null || intentResult.getContents() == null) {
            Timber.e("Cancelled scan");
            Toast.show("扫描未完成");
            return;
        }
        String text = intentResult.getContents();
        //机智云sdk参数
        String product_key = QrCodeUtil.getParamFromUrl(text, ScanCons.KEY_PRODUCT_KEY);
        String did = QrCodeUtil.getParamFromUrl(text, ScanCons.KEY_DID);
        String passCode = QrCodeUtil.getParamFromUrl(text, ScanCons.KEY_PASSCODE);
        //视频sdk参数
        String cidStr = QrCodeUtil.getParamFromUrl(text, ScanCons.KEY_CID_NUMBER);
        String username = QrCodeUtil.getParamFromUrl(text, ScanCons.KEY_USER_NAME);
        String password = QrCodeUtil.getParamFromUrl(text, ScanCons.KEY_PASSWORD);
        //判断二维码扫描数据是否正确
        if (product_key == null
                || did == null
                || passCode == null
                || cidStr == null
                || username == null
                || password == null) {
            Toast.showLong("二维码数据出错");
            return;
        }
        long cidNumber = Long.parseLong(cidStr);
        Timber.e("机智云参数: " + "product_key:\t" + product_key + "\tdid:\t" + did + "\tpasscode:\t" + passCode);
        Timber.e("视频sdk参数: " + "cidNumber:\t" + cidNumber + "\tusername:\t" + username + "\tpassword:\t" + password);
        //抛出扫描到设备的结果
        EventBus.getDefault().post(new ScanDeviceEvent(true, did, passCode, cidStr, username, password));
    }

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
        String content = QR_CODE_PREFIX +
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
        String content = QR_CODE_PREFIX +
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
