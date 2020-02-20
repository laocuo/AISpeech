package com.baidu.speech;

import android.content.Context;

import android.util.Log;
import com.baidu.speech.asr.BaiduAsr;
import com.baidu.speech.asr.IASRListener;
import com.baidu.speech.concurrent.ScheduledExecutorManger;
import com.baidu.speech.tts.BaiduTts;
import com.baidu.speech.tts.ITTSListener;

/**
 * 语音统一入口
 */
public class SpeechHelper implements ITTSListener, IASRListener {

    private final String TAG = "Speech";

    private SpeechHelper() {}

    private static SpeechHelper instance;

    public static synchronized SpeechHelper getInstance() {
        if (instance == null) {
            instance = new SpeechHelper();
        }
        return instance;
    }

    private boolean isSpeaking;

    private SpeechListener mSpeechListener;

    public SpeechHelper setContext(Context context) {
        BaiduAsr.getInstance().setContext(context);
        BaiduTts.getInstance().setContext(context);
        AndroidTts.getInstance().setContext(context);
        return this;
    }

    public void init() {
        BaiduAsr.getInstance().setListener(this).initAsr();
        BaiduTts.getInstance().setListener(this).initTts();
        AndroidTts.getInstance().initTts(this);
    }

    public void setSpeechListener(SpeechListener speechListener) {
        mSpeechListener = speechListener;
    }

    public boolean speak(String text) {
        return speak(text, "0");
    }

    public boolean speak(String text, String utteranceId) {
        return BaiduTts.getInstance().speak(text, utteranceId);
//        return AndroidTts.getInstance().speak(text, utteranceId);
    }

    public void stopTts() {
        BaiduTts.getInstance().stop();
        AndroidTts.getInstance().stop();
    }

    public void startWakeUp() {
        BaiduAsr.getInstance().startWakeUp();
    }

    public void release() {
        BaiduAsr.getInstance().release();
        BaiduTts.getInstance().release();
        AndroidTts.getInstance().release();
    }

    public void changeVoiceMode(String mode) {
        BaiduTts.getInstance().changeVoiceMode(mode);
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    @Override
    public void onPlayStart(String utteranceId) {
        Log.i(TAG, "onPlayStart " + utteranceId);
        isSpeaking = true;
        BaiduAsr.getInstance().stopRecog();
    }

    @Override
    public void onPlayDone(String utteranceId) {
        Log.i(TAG, "onPlayDone " + utteranceId);
        isSpeaking = false;
        if (utteranceId.equals("asr")) {
            ScheduledExecutorManger.getInstance().delayRun(new Runnable() {
                @Override
                public void run() {
                    BaiduAsr.getInstance().startRecog();
                }
            }, 2000);
        }
    }

    @Override
    public void onWakeUp() {
        Log.i(TAG, "onWakeUp");
        if (!isSpeaking) {
            speak("我在", "asr");
        }
    }

    @Override
    public void onRecogFinish(String voice) {
        Log.i(TAG, "onRecogFinish " + voice);
        if (mSpeechListener != null) {
            mSpeechListener.recogFinish(voice);
        }
        speak("收到", "asr");
    }

    @Override
    public void onAsrExit() {
        Log.i(TAG, "onAsrExit");
    }
}
