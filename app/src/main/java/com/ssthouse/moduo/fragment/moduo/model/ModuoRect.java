package com.ssthouse.moduo.fragment.moduo.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.ssthouse.moduo.control.util.DimenUtil;
import com.ssthouse.moduo.fragment.moduo.view.widget.ModuoView;


/**
 * 魔哆img占据的矩形
 * Created by ssthouse on 2016/1/28.
 */
public class ModuoRect{

    private Context mContext;
    private ModuoView moduoView;

    //控件最小高度
    private int outerMinHeight, outMaxHeight;
    //整个View的宽高
    private int outWidth, outHeight;
    //魔哆和控件宽度比
    private static final int SCALE_BIG = 2;
    private static final int SCALE_SMALL = 8;

    //魔哆四周位置
    private int left, top, right, bottom;
    //魔哆宽高
    private int moduoWidth, moduoHeight;
    //魔哆中心位置
    private int centerX, centerY;

    private Bitmap bigModuo;

    /**
     * 构造方法
     *
     * @param moduoView
     * @param outWidth
     * @param outMaxHeight 只能从外部view获得的最大高度
     */
    public ModuoRect(ModuoView moduoView, int outWidth, int outMaxHeight, Bitmap moduoBitmap) {
        this.moduoView = moduoView;
        this.mContext = moduoView.getContext();
        this.bigModuo = moduoBitmap;
        //最小高度---最大高度---宽度
        outerMinHeight = DimenUtil.dp2px(mContext, 100);
        this.outMaxHeight = outMaxHeight;
        this.outHeight = outMaxHeight;
        this.outWidth = outWidth;
        //计算魔哆大小
        resetDimens();
    }

    //根据外部宽高---重新计算魔哆大小
    private void resetDimens() {
        //当前控件宽高---主要用高度
        float k = (float) (outHeight - outerMinHeight) / (outMaxHeight - outerMinHeight);
        float dividerNumber = 1.0f / SCALE_SMALL + (1.0f / SCALE_BIG - 1.0f / SCALE_SMALL) * k;
        moduoWidth = (int) (outWidth * dividerNumber);
        moduoHeight = (int) (bigModuo.getHeight() / ((float) bigModuo.getWidth() / moduoWidth));
        //魔哆中心位置
        centerX = outWidth / 2;
        centerY = outHeight / 2;
    }

    //设置外围控件宽高
    public void setOuter(int outWidth, int outHeight) {
        this.outWidth = outWidth;
        this.outHeight = outHeight;
        //更新魔哆大小
        resetDimens();
    }

    public int getModuoWidth() {
        return moduoWidth;
    }

    public int getModuoHeight() {
        return moduoHeight;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    //getter-------------------setter-----------------------------------------------------------

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getOutWidth() {
        return outWidth;
    }

    public void setOutWidth(int outWidth) {
        this.outWidth = outWidth;
    }

    public int getOutHeight() {
        return outHeight;
    }

    public void setOutHeight(int outHeight) {
        this.outHeight = outHeight;
    }
}
