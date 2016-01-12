package com.ssthouse.gyroscope;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;

import com.ssthouse.gyroscope.representation.EulerAngles;

import timber.log.Timber;

/**
 * 陀螺仪管理器
 * Created by ssthouse on 2016/1/12.
 */
public class GyroscopeSensor {

    private Context context;

    /**
     * lib中的手机姿态管理器
     */
    private OrientationProvider orientationProvider;

    /**
     * 间隔时间
     */
    private int spaceTime = 200;

    /**
     * 监听器
     */
    private RotationChangeListener listener;

    private Handler handler = new Handler();

    /**
     * 手机姿态改变listener
     */
    public interface RotationChangeListener {
        /**
         * 魔哆三个方向的delta值
         *
         * @param deltaX
         * @param deltaY
         * @param deltaZ
         */
        void call(int deltaX, int deltaY, int deltaZ);
    }

    /**
     * 构造方法
     * @param context
     */
    public GyroscopeSensor(Context context) {
        this.context = context;

        //初始化控制器
        orientationProvider = new CalibratedGyroscopeProvider((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
    }


    /**
     * 定时执行的任务
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //获取数据
            EulerAngles eulerAngles = orientationProvider.getEulerAngles();
            Timber.e("%.2f    %.2f    %.2f", eulerAngles.getRoll(), eulerAngles.getPitch(), eulerAngles.getYaw());

            //计算出---三个轴的变化量

            //// TODO: 2016/1/12
            //将数据传递给监听器
            if (listener != null) {
                listener.call(0, 0, 0);
            }

            //重复发送
            handler.postDelayed(runnable, spaceTime);
        }
    };

    /**
     * 重置手机姿势
     */
    public void resetOrientation() {
        //停止发送消息
        handler.removeCallbacks(runnable);
        //重置手机状态
        orientationProvider = new CalibratedGyroscopeProvider((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
        //开启监听
        orientationProvider.start();
        //开启UI刷新
        handler.postDelayed(runnable, spaceTime);
    }

    /**
     * 启动手机姿态监听
     */
    public void start() {
        orientationProvider.start();
        handler.postDelayed(runnable, spaceTime);
    }

    /**
     * 暂停(如果activity被隐藏)
     */
    public void pause() {
        orientationProvider.stop();
        handler.removeCallbacks(runnable);
    }

    public RotationChangeListener getListener() {
        return listener;
    }

    public void setListener(RotationChangeListener listener) {
        this.listener = listener;
    }

    public int getSpaceTime() {
        return spaceTime;
    }

    public void setSpaceTime(int spaceTime) {
        this.spaceTime = spaceTime;
    }
}
