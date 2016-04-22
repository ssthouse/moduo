package com.mingko.moduo.control.util;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Sd卡工具类
 * Created by ssthouse on 2015/12/15.
 */
public class SDCardUtil {

    //单个数据块的大小（byte）
    private static long blockSize ;
    //SDCard 总计数据块数
    private static long totalBlocks ;
    //SDCard 剩余可用数据块数
    private static long availableBlocks ;

    /**
     * 静态初始化属性
     * 根据版本使用方法，API>=18的版本用新的方法，否则使用已过时的方法
     * 似乎可以继续优化的代码
     */
    static{
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
                availableBlocks = stat.getAvailableBlocksLong();
            }
            else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
                availableBlocks = stat.getAvailableBlocks();
            }
        }else{
            blockSize = 0;
            totalBlocks = 0;
            availableBlocks = 0;
        }
    }

    /**
     * 判断SDCard是否可用
     *
     * @return 可用返回true 否则返回false
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SDCard路径
     *
     * @return SDCard目录
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取SDCard总容量 单位byte
     *
     * @return SDCard总容量
     */
    public static long getSDCardSize(){
        return blockSize * totalBlocks;
    }

    /**
     * 获取SDCard的剩余容量 单位byte
     *
     * @return SDCard的剩余容量
     */
    public static long getSDCardLastSize() {
        return blockSize * availableBlocks;
    }

    /**
     * 获取系统存储路径
     *
     * @return 系统存储路径
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }
}
