package com.baidu.speech.tts.listener;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 在 MessageListener的基础上，和UI配合。
 * Created by fujiayi on 2017/9/14.
 */

public class UiMessageListener extends MessageListener {

    private Handler mainHandler;

    private static final String TAG = "UiMessageListener";

    public UiMessageListener(Handler mainHandler) {
        super();
        this.mainHandler = mainHandler;
    }

    @Override
    public void onSpeechStart(String utteranceId) {
//        sendMessage("播放开始回调, 序列号:" + utteranceId);
        sendMessage(utteranceId, false, PLAY_START);
    }

    /**
     * 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSpeechFinish(String utteranceId) {
//        sendMessage("播放结束回调, 序列号:" + utteranceId);
        sendMessage(utteranceId, false, PLAY_DONE);
    }

    /**
     * 合成数据和进度的回调接口，分多次回调。
     * 注意：progress表示进度，与播放到哪个字无关
     * @param utteranceId
     * @param data 合成的音频数据。该音频数据是采样率为16K，2字节精度，单声道的pcm数据。
     * @param progress 文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] data, int progress) {
        // sendMessage("onSynthesizeDataArrived");
        mainHandler.sendMessage(mainHandler.obtainMessage(UI_CHANGE_SYNTHES_TEXT_SELECTION, progress, 0));
    }

    /**
     * 播放进度回调接口，分多次回调
     * 注意：progress表示进度，与播放到哪个字无关
     *
     * @param utteranceId
     * @param progress 文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        // sendMessage("onSpeechProgressChanged");
        mainHandler.sendMessage(mainHandler.obtainMessage(UI_CHANGE_INPUT_TEXT_SELECTION, progress, 0));
    }

    protected void sendMessage(String message) {
        sendMessage(message, false);
    }

    @Override
    protected void sendMessage(String message, boolean isError) {
        sendMessage(message, isError, PRINT);
    }

    protected void sendMessage(String message, boolean isError, int action) {
        super.sendMessage(message, isError);
        if (action == PRINT) {
            Log.i(TAG, message);
        } else {
            if (mainHandler != null) {
                Message msg = Message.obtain();
                msg.what = action;
                msg.obj = message;
                mainHandler.sendMessage(msg);
            }
        }
    }
}