package com.baidu.aip.asrwakeup3.core;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.aip.asrwakeup3.core.recog.IStatus;
import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;
import com.baidu.aip.asrwakeup3.core.wakeup.MyWakeup;
import com.baidu.aip.asrwakeup3.core.wakeup.listener.IWakeupListener;
import com.baidu.aip.asrwakeup3.core.wakeup.listener.RecogWakeupListener;
import com.baidu.speech.asr.SpeechConstant;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaiduAsr implements IStatus {

    public interface BaiduAsrInterface {
        void onWakeUp();
        void onSpeechTake(String voice);
    }

    private BaiduAsr() {
    }

    private void handle(Message msg) {
        switch (msg.what) {
            case STATUS_WAKEUP_SUCCESS:
                startRecog();
                if (mBaiduAsrInterface != null) {
                    mBaiduAsrInterface.onWakeUp();
                }
                break;
            case MSG_SPEECH_CONTENT:
                String voice = (String) msg.obj;
                if (mBaiduAsrInterface != null) {
                    mBaiduAsrInterface.onSpeechTake(voice);
                }
                break;
            default:
                int status = msg.what;
                String message = (String) msg.obj;
                Log.d(TAG, "status:" + status + "||" + message);
                break;
        }
    }

    private static class SingletonInner {
        private static BaiduAsr singletonStaticInner = new BaiduAsr();
    }

    public synchronized static BaiduAsr getInstance() {
        return SingletonInner.singletonStaticInner;
    }

    private Context mContext;

    private Handler mainHandler;

    private BaiduAsrInterface mBaiduAsrInterface;

    private static final String TAG = "BaiduAsr";

    public BaiduAsr setContext(Context context) {
        mContext = context;
        return this;
    }

    public BaiduAsr setBaiduAsrInterface(BaiduAsrInterface baiduAsrInterface) {
        mBaiduAsrInterface = baiduAsrInterface;
        return this;
    }

    private MyWakeup myWakeup;

    public void initWakeUp() {
        if (mainHandler == null) {
            mainHandler = new Handler(mContext.getMainLooper()) {
                /*
                 * @param msg
                 */
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    handle(msg);
                }

            };
        }
        // 基于DEMO唤醒词集成第1.1, 1.2, 1.3步骤
        IWakeupListener listener = new RecogWakeupListener(mainHandler);
        myWakeup = new MyWakeup(mContext, listener);
    }

    // 点击“开始识别”按钮
    // 基于DEMO唤醒词集成第2.1, 2.2 发送开始事件开始唤醒
    public void startWakeUp() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        // params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        // params.put(SpeechConstant.IN_FILE,"res:///com/baidu/android/voicedemo/wakeup.pcm");
        // params里 "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下
        myWakeup.start(params);
    }

    // 基于DEMO唤醒词集成第4.1 发送停止事件
    public void stopWakeUp() {
        myWakeup.stop();
    }

    public void releaseWakeUp() {
        // 基于DEMO唤醒词集成第5 退出事件管理器
        myWakeup.release();
    }

    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    private MyRecognizer myRecognizer;

    /**
     * 0: 方案1， backTrackInMs > 0,唤醒词说完后，直接接句子，中间没有停顿。
     *              开启回溯，连同唤醒词一起整句识别。推荐4个字 1500ms
     *          backTrackInMs 最大 15000，即15s
     *
     * >0 : 方案2：backTrackInMs = 0，唤醒词说完后，中间有停顿。
     *       不开启回溯。唤醒词识别回调后，正常开启识别。
     * <p>
     *
     */
    private int backTrackInMs = 1500;

    public void initRecog() {
        if (mainHandler == null) {
            mainHandler = new Handler(mContext.getMainLooper()) {
                /*
                 * @param msg
                 */
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    handle(msg);
                }

            };
        }
        Log.d(TAG, "initRecog");
        IRecogListener recogListener = new MessageStatusRecogListener(mainHandler);
        // 改为 SimpleWakeupListener 后，不依赖handler，但将不会在UI界面上显示
        myRecognizer = new MyRecognizer(mContext, recogListener);
    }

    public void startRecog() {
        Log.d(TAG, "startRecog");
        // 此处 开始正常识别流程
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        // 如识别短句，不需要需要逗号，使用1536搜索模型。其它PID参数请看文档
        params.put(SpeechConstant.PID, 1536);
        if (backTrackInMs > 0) {
            // 方案1  唤醒词说完后，直接接句子，中间没有停顿。开启回溯，连同唤醒词一起整句识别。
            // System.currentTimeMillis() - backTrackInMs ,  表示识别从backTrackInMs毫秒前开始
            params.put(SpeechConstant.AUDIO_MILLS, System.currentTimeMillis() - backTrackInMs);
        }
        myRecognizer.cancel();
        myRecognizer.start(params);
    }

    public void stopRecog() {
        Log.d(TAG, "stopRecog");
        myRecognizer.stop();
    }

    public void releaseRecog() {
        Log.d(TAG, "releaseRecog");
        myRecognizer.release();
    }

    public void initAsr() {
        mainHandler = new Handler(mContext.getMainLooper()) {
            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handle(msg);
            }

        };
        initRecog();
        initWakeUp();
    }

    public void start() {
        startWakeUp();
    }

    public void stop() {
        stopWakeUp();
        stopRecog();
    }

    public void release() {
        releaseRecog();
        releaseWakeUp();
    }
}
