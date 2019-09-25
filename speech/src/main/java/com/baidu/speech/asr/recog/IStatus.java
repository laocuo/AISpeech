package com.baidu.speech.asr.recog;

/**
 * Created by fujiayi on 2017/6/14.
 */

public interface IStatus {

    int STATUS_NONE = 2;

    int STATUS_READY = 3;
    int STATUS_SPEAKING = 4;
    int STATUS_RECOGNITION = 5;

    int STATUS_FINISHED = 6;
    int STATUS_LONG_SPEECH_FINISHED = 7;
    int STATUS_STOPPED = 10;

    int STATUS_WAITING_READY = 8001;
    int WHAT_MESSAGE_STATUS = 9001;

    int STATUS_WAKEUP_SUCCESS = 7001;
    int STATUS_WAKEUP_EXIT = 7003;

    //================自定义扩展消息================//
    int MSG_RECOG_FINISH = 10000;
    int MSG_ASR_EXIT       = 10001;
    //================自定义扩展消息================//
}
