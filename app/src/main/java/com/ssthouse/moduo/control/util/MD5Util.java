package com.ssthouse.moduo.control.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

/**
 * 加密工具
 * Created by ssthouse on 2016/1/20.
 */
public class MD5Util {

    /**
     * 获取加密后字符串
     *
     * @param plainText
     */
    public static String getMdStr(String plainText) {
        StringBuffer buf = new StringBuffer("");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            Timber.e("result: " + buf.toString());//32位的加密
            Timber.e("result: " + buf.toString().substring(8, 24));//16位的加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
}
