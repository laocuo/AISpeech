package com.baidu.speech.asr;


public interface IASRListener {
    void onWakeUp();
    void onRecogFinish(String voice);
    void onAsrExit();
}
