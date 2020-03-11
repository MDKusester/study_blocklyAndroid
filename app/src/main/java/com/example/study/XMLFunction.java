package com.example.study;

import android.util.Log;

public class XMLFunction {

    public static int state = 0;

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

    public synchronized void stop() {

        Log.d("Tag", "停止运动");
    }

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
    public synchronized String stateLed() {
        if(state == 0) {
            Log.d("Tag", "LED状态是TRUE");
            return "TRUE";
        }else {
            Log.d("Tag", "LED状态是FALSE");
            return "FALSE";
        }
    }

    public synchronized void wait(int time) {
        Log.d("Tag", "等待"+time+"秒");
    }


}
