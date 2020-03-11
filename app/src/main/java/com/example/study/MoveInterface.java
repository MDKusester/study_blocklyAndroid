package com.example.study;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class MoveInterface {
    private Activity activity;

    public MoveInterface(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public synchronized void goBackTime(final int goBack, final int speed, final int time) {
        switch (goBack) {
            case 1:
                Log.d("Tag", "向前以" + speed + "的速度奔跑" + time + "秒");
                break;
            case 2:
                Log.d("Tag", "向后以" + speed + "的速度奔跑" + time + "秒");
                break;
            default:
                Log.d("Tag", "未知指令");
                break;
        }
    }

    @JavascriptInterface
    public synchronized void clockwise(final int direction, final int speed, final int time) {
        switch (direction) {
            case 1:
                Log.d("Tag", "顺时针以" + speed + "的速度奔跑" + time + "秒");
                break;
            case 2:
                Log.d("Tag", "逆时针以" + speed + "的速度奔跑" + time + "秒");
                break;
            default:
                Log.d("Tag", "未知指令");
                break;
        }
    }

    @JavascriptInterface
    public synchronized void goBack(final int direction, final int speed) {
        switch (direction) {
            case 1:
                Log.d("Tag", "向前以" + speed + "的速度奔跑");
                break;
            case 2:
                Log.d("Tag", "向后以" + speed + "的速度奔跑");
                break;
            default:
                Log.d("Tag", "未知指令");
                break;
        }
    }

    @JavascriptInterface
    public synchronized void direction(final int direction, final int speed) {
        switch (direction) {
            case 1:
                Log.d("Tag", "以" + speed + "的速度左转");
                break;
            case 2:
                Log.d("Tag", "以" + speed + "的速度右转");
                break;
            default:
                Log.d("Tag", "未知指令");
                break;
        }
    }

    @JavascriptInterface
    public synchronized void stop() {

        Log.d("Tag", "停止运动");
    }

    @JavascriptInterface
    public synchronized void openLed(final int state) {
        switch (state) {
            case 1:
                Log.d("Tag", "打开LED灯");
                break;
            case 2:
                Log.d("Tag", "关闭LED灯");
                break;
            default:
                Log.d("Tag", "未知指令");
                break;
        }
    }
    @JavascriptInterface
    public synchronized void stateLed() {
        Log.d("Tag", "LED状态");
    }
    @JavascriptInterface
    public synchronized void wait(int time) {
        Log.d("Tag", "等待"+time+"秒");
    }
}
