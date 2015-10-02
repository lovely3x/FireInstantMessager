package com.lovely3x.common.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by lovely3x on 15-7-30.
 * activity管理器
 * 主要用来记录启动了的activity,和她的状态
 */
public class ActivityManager {

    private static final String TAG = "ActivityManager";

    /**
     * 启动状态 等待回复
     */
    public static final int LAUNCH_STATE_WAITING_REPLY = 0x1;

    /**
     * 启动状态 启动成功
     */
    public static final int LAUNCH_STATE_OK = 0x2;

    /**
     * 等待activity 响应超时 ,不管他(如果可以就将它清除掉,可惜碰不到它)
     */
    public static final int LAUNCH_STATE_TIME_OUT = 0x3;
    /**
     * 下面的7个状态对应了 activity中相应的状态
     */
    public static final int ACTIVITY_STATE_ON_CREATE = 0X1;
    public static final int ACTIVITY_STATE_ON_START = 0X2;
    public static final int ACTIVITY_STATE_ON_RESUME = 0X3;
    public static final int ACTIVITY_STATE_ON_RESTART = 0X4;
    public static final int ACTIVITY_STATE_ON_PAUSE = 0X5;
    public static final int ACTIVITY_STATE_ON_STOP = 0X6;
    public static final int ACTIVITY_STATE_ON_DESTROY = 0X7;
    /**
     * 所有的activity表
     */
    public static final HashMap<Activity, Integer> allActivityStatus = new HashMap<>();

    /**
     * 启动超时
     */
    private static final long TIME_OUT = 1000 * 3;

    /**
     * 上一个更新状态的activity
     */
    public static ActivityWrapper previousActivity;
    /**
     * 当前的状态
     */
    private static int currentState = LAUNCH_STATE_OK;
    /**
     * 计时器
     */
    private static CountDownTimer countDownTimer;

    /**
     * 更新activity的状态
     *
     * @param activity 需要更新的activity
     * @param state    需要更新的状态
     */
    public static void updateState(Activity activity, Integer state) {
        //  Log.e(TAG, "update state -> " + state);
        synchronized (ActivityManager.class) {
            updateStopRecord(activity, state);
            switch (state) {
                case ACTIVITY_STATE_ON_CREATE:
                case ACTIVITY_STATE_ON_START:
                case ACTIVITY_STATE_ON_RESUME:
                case ACTIVITY_STATE_ON_RESTART:
                case ACTIVITY_STATE_ON_PAUSE:
                case ACTIVITY_STATE_ON_STOP:
                    allActivityStatus.put(activity, state);
                    break;
                case ACTIVITY_STATE_ON_DESTROY:
                    allActivityStatus.remove(activity);
                    break;
            }
        }
    }

    /**
     * 更新停止记录
     *
     * @param activity 被停止的activity
     */
    static void updateStopRecord(Activity activity, int state) {
        synchronized (ActivityManager.class) {
            previousActivity = new ActivityWrapper(activity, state);
        }
    }

    /**
     * 是否是从后台进入到前台,你可能需要在onReStart处中调用
     * 但是,这个方法无法判断应用第一次进入从后台转入前台的这种情况
     * <p>
     * 如果是第一次的话应该判断是否activity栈中只有一个activity
     *
     * @param activity 执行判断的activity
     * @return true if the activity from background to foreground,false otherwise
     */
    public static boolean isFromBackgroundToForeground(Activity activity) {
        synchronized (ActivityManager.class) {
            String currentName = activity.getClass().getName();
            if (previousActivity == null) {
                return false;
            }
            if (currentName.equals(previousActivity.activity) && (previousActivity.state == ACTIVITY_STATE_ON_STOP || previousActivity.state == ACTIVITY_STATE_ON_DESTROY)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 获取指定的activity的状态
     *
     * @param activity 需要获取状态的activity
     * @return ACTIVITY_STATE_INVALID 如果没有该activity 否则 范湖对应的状态
     */
    public static Integer getActivityState(Activity activity) {
        synchronized (ActivityManager.class) {
            Integer state = allActivityStatus.get(activity);
            return state == null ? ACTIVITY_STATE_ON_DESTROY : state;
        }

    }

    /**
     * 启动指定的界面
     *
     * @param compoundsClazz         需要启动的activity组件名
     * @param bundle                 需要传递的数据
     * @param launchBeforeClearStack 启动之前先清除栈数据
     */
    public static void launchActivity(Activity context, Class<? extends Activity> compoundsClazz, Bundle bundle, boolean launchBeforeClearStack) {
        synchronized (ActivityManager.class) {
            switch (currentState) {
                case LAUNCH_STATE_OK:
                case LAUNCH_STATE_TIME_OUT:
                    Intent intent = new Intent(context, compoundsClazz);
                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                    if (launchBeforeClearStack) {
                        finishAll();
                    }
                    //更新状态
                    currentState = LAUNCH_STATE_WAITING_REPLY;
                    initCountDownTimer();
                    context.startActivity(intent);
                    break;
                case LAUNCH_STATE_WAITING_REPLY:
                    Log.e(TAG, "waiting activity reply,so can't launch other activity");
                    break;
            }
        }
    }


    /**
     * 启动界面
     *
     * @param context                activity
     * @param compoundsClazz         需要启动的activity组件
     * @param bundle                 需要传递的数据过去
     * @param launchBeforeClearStack 启动前,是否清除掉activity栈
     * @param requestCode            请求码
     */
    public static void launchActivityForResult(Activity context, Class<? extends Activity> compoundsClazz, Bundle bundle, boolean launchBeforeClearStack, int requestCode) {
        synchronized (ActivityManager.class) {
            switch (currentState) {
                case LAUNCH_STATE_OK:
                case LAUNCH_STATE_TIME_OUT:
                    Intent intent = new Intent(context, compoundsClazz);
                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                    if (launchBeforeClearStack) {
                        finishAll();
                    }
                    //更新状态
                    currentState = LAUNCH_STATE_WAITING_REPLY;
                    initCountDownTimer();
                    context.startActivityForResult(intent, requestCode);
                    break;
                case LAUNCH_STATE_WAITING_REPLY:
                    Log.e(TAG, "waiting activity reply,so can't launch other activity");
                    break;
            }
        }
    }


    /**
     * activity被启动后的回执
     */
    public static void launchReply() {
        synchronized (ActivityManager.class) {
            currentState = LAUNCH_STATE_OK;
            Log.e(TAG, "update current state launchReply");
            if (countDownTimer != null) countDownTimer.cancel();
        }
    }

    /**
     * activity启动超时后执行
     */
    public static void launchTimeOut() {
        synchronized (ActivityManager.class) {
            Log.e(TAG, "update current state launch timeout");
            currentState = LAUNCH_STATE_TIME_OUT;
            if (countDownTimer != null) countDownTimer.cancel();
        }
    }

    /**
     * 获取当前所有的activity
     *
     * @return activities
     */
    public static List<Activity> getActivities() {
        return new ArrayList<>(allActivityStatus.keySet());
    }

    /**
     * 初始化计时器
     */
    private static void initCountDownTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(TIME_OUT, TIME_OUT) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                launchTimeOut();
            }
        };
        countDownTimer.start();
    }


    /**
     * 结束掉所有的activity
     */
    public static void finishAll() {
        finishAllExceptThis(null);
    }

    /**
     * finish掉除指定的activity外的所有activity
     *
     * @param activity 不需要移除的对象
     */
    public static void finishAllExceptThis(Class<? extends Activity> activity) {
        synchronized (ActivityManager.class) {
            Set<Activity> keySet = allActivityStatus.keySet();
            Iterator<Activity> it = keySet.iterator();
            while (it.hasNext()) {
                Activity next = it.next();
                if (next != null) {
                    if (activity != null) {
                        if (!activity.getName().equals(next.getClass().getName())) {
                            it.remove();
                            next.finish();
                        }
                    } else {
                        it.remove();
                        next.finish();
                    }
                }
            }
        }
    }

    /**
     * activity 包装类
     */
    public static class ActivityWrapper {
        private final int state;
        private final String activity;

        public ActivityWrapper(Activity activity, int state) {
            this.activity = activity.getClass().getName();
            this.state = state;
        }
    }

}
