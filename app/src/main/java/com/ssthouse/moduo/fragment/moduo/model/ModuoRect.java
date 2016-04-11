package com.ssthouse.moduo.fragment.moduo.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.ssthouse.moduo.control.util.DimenUtil;
import com.ssthouse.moduo.fragment.moduo.view.widget.ModuoView;


/**
 * 魔哆img占据的矩形
 * Created by ssthouse on 2016/1/28.
 */
public class ModuoRect implements IModuoControl {

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

    //消息常量(控制魔哆动画)
    private static final int MSG_WALK = 1000;
    private static final int MSG_WALK_SPACE = 2;

    //是不是往左走
    private boolean isWalkLeft = true;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WALK:
                    if (isWalkLeft) {
                        walkLeft();
                    } else {
                        walkRight();
                    }
                    sendEmptyMessageDelayed(MSG_WALK, MSG_WALK_SPACE);
                    break;
            }
            moduoView.invalidate();
        }
    };

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

    //开始走路
    @Override
    public void startWalk() {
        handler.sendEmptyMessageDelayed(MSG_WALK, MSG_WALK_SPACE);
    }

    //往左走
    private void walkLeft() {
        //步长
        int stepLength = outWidth / 200;
        if (centerX > 0) {
            centerX -= stepLength;
        } else {
            //向右走
            isWalkLeft = false;
        }
    }

    //往右走
    private void walkRight() {
        //步长
        int stepLength = outWidth / 200;
        if (centerX < outWidth) {
            centerX += stepLength;
        } else {
            //向右走
            isWalkLeft = true;
        }
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
