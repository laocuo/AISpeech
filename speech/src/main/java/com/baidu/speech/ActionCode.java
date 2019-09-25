package com.baidu.speech;

/**
 * Created by liulj on 17-12-8.
 */

public interface ActionCode {

    int PRINT = 10000;

    int UI_CHANGE_INPUT_TEXT_SELECTION = 10010;

    int UI_CHANGE_SYNTHES_TEXT_SELECTION = 10020;

    int INIT_SUCCESS = 10030;

    int INIT_FAIL = 10031;

    int INIT_TTS_SUCCESS = 10032;

    /*******
     * 唤醒成功
     ***********/
    int WHAT_WAKE_UP_SUCCESS_CODE = 10040;

    /*******
     * 唤醒失败
     ***********/
    int WHAT_WAKE_UP_FAILED_CODE = 10050;

    /*******
     * 播放进度
     ***********/
    int WHAT_TTS_PALY_PROGRESS_CODE = 10060;

    /*******
     * 语音合成进度
     ***********/
    int WHAT_SYNTHETIZE_PROGRESS_CODE = 10070;

    /*******
     * 语音播放开始
     ***********/
    int WHAT_TTS_START_CODE = 10080;

    /*******
     * 语音播放结束
     ***********/
    int WHAT_TTS_FINISH_CODE = 10090;

    /*******
     * 引擎就绪，可以开始说话
     ***********/
    int WHAT_ASR_READY_CODE = 10100;

    /*******
     * 检测到用户说话
     ***********/
    int WHAT_CHECK_SPEAK_START_CODE = 10110;

    /*******
     * 检测到用户说话结束
     ***********/
    int WHAT_CHECK_SPEAK_FINISH_CODE = 10120;

    /*******
     * 临时识别结果
     ***********/
    int WHAT_TEMP_SPEECH_RESULT_CODE = 10130;

    /*******
     * 识别结束
     ***********/
    int WHAT_FINAL_SPEECH_RESULT_CODE = 10140;

    /*******
     * 识别错误
     ***********/
    int WHAT_SPEECH_ERROR_CODE = 10150;

    /*******
     * 原始语义识别结果
     ***********/
    int WHAT_ORG_SPEECH_RESULT_CODE = 10160;

    /*******
     * 识别一段话结束。如果是长语音的情况会继续识别下段话。
     ***********/
    int WHAT_SPEECH_FINISH_CODE = 10170;

    /*******
     * 长语音识别结束。
     ***********/
    int WHAT_SPEECH_LONG_FINISH_CODE = 10180;

    /*******
     * 离线资源加载成功
     ***********/
    int WHAT_OFFLINE_LOADED_CODE = 10190;

    /*******
     * 离线资源卸载成功
     ***********/
    int WHAT_OFFLINE_UNLOADED_CODE = 10200;

    /*******
     * 识别引擎结束并空闲中
     ***********/
    int WHAT_SPEECH_EXIT_CODE = 10210;


    /**************************************业务指令****************************************/
    /***
     * 语音控制设备功能
     */
    int WHAT_CMD_REBOOT_CODE = 30000;//系统重启

    int WHAT_CMD_SHUT_DOWN_CODE = 30001;//关闭系统

    int WHAT_CMD_UPDATE_CODE = 30002;//版本升级

    int WHAT_CMD_VOLUME_RAISE_CODE = 30003;//声音升高

    int WHAT_CMD_VOLUME_LOWER_CODE = 30004;//声音降低

    /***
     * 语音控制产品功能
     */
    int WHAT_CMD_OPEN_DEBUG_CODE = 31001;//打开debug日志

    int WHAT_CMD_CLOSE_DEBUG_CODE = 31002;//关闭debug日志

    int WHAT_CMD_OPEN_SETTER_CODE = 31003;//打开设置页面

    int WHAT_CMD_EXIT_SETTER_CODE = 31004;//关闭设置页面

    int WHAT_ROBOT_DUODUO_EXIT_CODE = 31005;//多多退出

    /***
     * 语音控制业务
     */
    int WHAT_BUSINESS_DO_BASE_TRAIN_CODE = 32000;//起步停车训练

    int WHAT_BUSINESS_DO_DCRK_CODE = 32001;//执行倒车入库训练

    int WHAT_BUSINESS_DO_CFTC_CODE = 32002;//执行侧方停车训练

    int WHAT_BUSINESS_DO_PDQB_CODE = 32003;//执行坡道起步训练

    int WHAT_BUSINESS_DO_ZJZW_CODE = 32004;//执行直角转弯训练

    int WHAT_BUSINESS_DO_QXXS_CODE = 32005;//执行曲线行驶训练

    int WHAT_BUSINESS_DO_ZHMN_CODE = 32007;//执行综合模拟训练

    int WHAT_BUSINESS_DO_FINISH_TRAIN_CODE = 32100;//结束训练

    int WHAT_NOTIFY_COACH_CODE = 32101;//通知教练

    int WHAT_BUSINESS_DO_QBTC_CODE = 32102;//起步停车

    /***
     * 语音控制视频教程
     */
    int WHAT_OPEN_VIDEO_TEACHING_CODE = 33000;//视频教学
    int WHAT_KNOW_CAR_TEACHING_CODE = 33001;//辨识视频
    int WHAT_KNOW_WHEEL_TEACHING_CODE = 33002;//转向盘视频
    int WHAT_KNOW_CLUTCH_TEACHING_CODE = 33003;//离合器视频
    int WHAT_KNOW_FOOT_BRAKE_TEACHING_CODE = 33004;//制动踏板视频
    int WHAT_KNOW_ACCELERATOR_TEACHING_CODE = 33005;//加速踏板视频
    int WHAT_KNOW_TRANSMISSION_TEACHING_CODE = 33006;//变速器操纵杆

    int WHAT_KNOW_HAND_BREAK_TEACHING_CODE = 33007;//驻车制动器操纵杆视频
    int WHAT_KNOW_QBTC_TEACHING_CODE = 33008;//起步及停车训练视频
    int WHAT_KNOW_SCQGC_TEACHING_CODE = 33009;//上车前观察视频
    int WHAT_KNOW_SXCDZ_TEACHING_CODE = 33100;//上下车动作视频
    int WHAT_KNOW_TZZY_TEACHING_CODE = 33101;//调整座椅视频
    int WHAT_KNOW_SAFE_BELT_TEACHING_CODE = 33102;//安全带操纵视频
    int WHAT_KNOW_MIRROR_TEACHING_CODE = 33103;//调整后视镜视频

}
