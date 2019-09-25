package com.baidu.speech.concurrent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 后台任务管理类
 * 用于执行各种后台定时任务
 */
public class ScheduledExecutorManger {
    private ScheduledExecutorManger() {}

    private static ScheduledExecutorManger mExecutorManger;

    private ScheduledExecutorService mExecutorService;

    public static ScheduledExecutorManger getInstance() {

        if (mExecutorManger == null) {
            synchronized (ScheduledExecutorManger.class) {
                if (mExecutorManger == null) {
                    mExecutorManger = new ScheduledExecutorManger();
                    mExecutorManger.init();
                }
            }
        }

        return mExecutorManger;
    }

    private void init() {
        if (mExecutorService == null) {
            mExecutorService = new ScheduledThreadPoolExecutor(4);
        }
    }

    public void delayRun(Runnable r, long delay) {
        if (mExecutorService != null) {
            mExecutorService.schedule(r, delay, TimeUnit.MILLISECONDS);
        }
    }

    public void scheduledRun(Runnable r, long delay) {
        if (mExecutorService != null) {
            mExecutorService.scheduleWithFixedDelay(r, delay, delay, TimeUnit.MILLISECONDS);
        }
    }
}

