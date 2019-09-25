package com.baidu.speech.tts;


public interface ITTSListener {
    void onPlayStart(String utteranceId);
    void onPlayDone(String utteranceId);
}
