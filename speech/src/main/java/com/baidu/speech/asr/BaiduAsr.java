package com.baidu.speech.asr;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import com.baidu.speech.asr.recog.IStatus;
import com.baidu.speech.asr.recog.MyRecognizer;
import com.baidu.speech.asr.recog.listener.IRecogListener;
import com.baidu.speech.asr.recog.listener.MessageStatusRecogListener;
import com.baidu.speech.asr.wakeup.MyWakeup;
import com.baidu.speech.asr.wakeup.listener.IWakeupListener;
import com.baidu.speech.asr.wakeup.listener.RecogWakeupListener;
import com.baidu.speech.asr.SpeechConstant;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaiduAsr implements IStatus {

    private BaiduAsr() {
    }

    private boolean inited;

    private void handle(Message msg) {
        switch (msg.what) {
            case STATUS_WAKEUP_SUCCESS:
                if (mListener != null) {
                    mListener.onWakeUp();
                }
                break;
            case MSG_RECOG_FINISH:
                String voice = (String) msg.obj;
                if (mListener != null) {
                    mListener.onRecogFinish(voice);
                }
                break;
            case MSG_ASR_EXIT:
                if (mListener != null) {
                    mListener.onAsrExit();
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

    private IASRListener mListener;

    private static final String TAG = "Speech";

    public BaiduAsr setContext(Context context) {
        mContext = context;
        return this;
    }

    public BaiduAsr setListener(IASRListener l) {
        mListener = l;
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
        Log.d(TAG, "initWakeUp");
        // 基于DEMO唤醒词集成第1.1, 1.2, 1.3步骤
        IWakeupListener listener = new RecogWakeupListener(mainHandler);
        myWakeup = new MyWakeup(mContext, listener);
    }

    // 点击“开始识别”按钮
    // 基于DEMO唤醒词集成第2.1, 2.2 发送开始事件开始唤醒
    public void startWakeUp() {
        Log.d(TAG, "startWakeUp");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SpeechConstant.APP_ID, "16923439"); //当报读取不到APPID错误时,只能在这里主动传入
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
        inited = true;
    }

    public void startRecog() {
        if (!inited) return;
        Log.d(TAG, "startRecog");
        // 此处 开始正常识别流程
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 4000);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets:///baidu_speech_grammar.bsg");
        params.put(SpeechConstant.NLU, "enable");
        params.put(SpeechConstant.DECODER, 2);
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
        if (!inited) return;
        Log.d(TAG, "stopRecog");
        myRecognizer.cancel();
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

    public void release() {
        stopRecog();
        stopWakeUp();
        releaseRecog();
        releaseWakeUp();
    }
}
