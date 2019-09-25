package com.baidu.speech.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 后台任务管理类
 * 用于执行各种后台耗时任务
 */
public class ExecutorManger {

    private ExecutorManger() {}

    private static ExecutorManger mExecutorManger;

    private ExecutorService mExecutorService;

    public static ExecutorManger getInstance() {

        if (mExecutorManger == null) {
            synchronized (ExecutorManger.class) {
                if (mExecutorManger == null) {
                    mExecutorManger = new ExecutorManger();
                    mExecutorManger.init();
                }
            }
        }

        return mExecutorManger;
    }

    private void init() {
        if (mExecutorService == null) {
            mExecutorService = new ThreadPoolExecutor(8, 16,
                    10L, TimeUnit.MILLISECONDS,
                    new SynchronousQueue<Runnable>());
        }
    }

    public void run(Runnable r) {
        if (mExecutorService != null) {
            mExecutorService.execute(r);
        }
    }
}

