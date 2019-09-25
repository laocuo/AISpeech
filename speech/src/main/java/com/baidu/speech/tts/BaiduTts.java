package com.baidu.speech.tts;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.baidu.speech.tts.control.InitConfig;
import com.baidu.speech.tts.control.MySyntherizer;
import com.baidu.speech.tts.control.NonBlockSyntherizer;
import com.baidu.speech.tts.listener.UiMessageListener;
import com.baidu.speech.tts.util.OfflineResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BaiduTts implements MainHandlerConstant {

    private BaiduTts() {
    }

    private static class SingletonInner {
        private static BaiduTts singletonStaticInner = new BaiduTts();
    }

    public synchronized static BaiduTts getInstance() {
        return SingletonInner.singletonStaticInner;
    }

    // ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    protected String appId = "";

    protected String appKey = "";

    protected String secretKey = "";

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.MIX;

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    protected String offlineVoice = OfflineResource.VOICE_MALE;

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;

    protected static String DESC = "请先看完说明。之后点击“合成并播放”按钮即可正常测试。\n"
            + "测试离线合成功能需要首次联网。\n"
            + "纯在线请修改代码里ttsMode为TtsMode.ONLINE， 没有纯离线。\n"
            + "本Demo的默认参数设置为wifi情况下在线合成, 其它网络（包括4G）使用离线合成。 在线普通女声发音，离线男声发音.\n"
            + "合成可以多次调用，SDK内部有缓存队列，会依次完成。\n\n";

    protected Context mContext;

    protected Handler mainHandler;

    private ITTSListener mListener;

    private static final String TAG = "Speech";

    public BaiduTts setContext(Context context) {
        mContext = context;
        return this;
    }

    public BaiduTts setListener(ITTSListener l) {
        mListener = l;
        return this;
    }

    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    public void initTts() {
        Log.i(TAG, "initialTts");
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
        LoggerProxy.printable(true); // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);

        Map<String, String> params = getParams();

        appId = getMetaDataFromApp("com.baidu.speech.APP_ID");
        appKey = getMetaDataFromApp("com.baidu.speech.API_KEY");
        secretKey = getMetaDataFromApp("com.baidu.speech.SECRET_KEY");

        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);

        synthesizer = new NonBlockSyntherizer(mContext, initConfig, mainHandler); // 此处可以改为MySyntherizer 了解调用过程
        Log.i(TAG, "initialTts success");
    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_NETWORK);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(mContext, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
        }
        return offlineResource;
    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    public boolean speak(String text) {
        return speak(text, "0");
    }

    public boolean speak(String text, String msgId) {
        // 需要合成的文本text的长度不能超过1024个GBK字节。
        if (TextUtils.isEmpty(text)) {
            text = "百度语音，面向广大开发者永久免费开放语音合成技术。";
            return false;
        }
        Log.i(TAG, "speak:" + text);
        // 合成前可以修改参数：
        // Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        int result = synthesizer.speak(text, msgId);
        checkResult(result, "speak");
        return true;
    }

    /**
     * 切换在线发音
     */
    public void loadOnlineMode(String mode) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SpeechSynthesizer.PARAM_SPEAKER, mode);
        synthesizer.setParams(params);
    }

    /**
     * 切换离线发音。注意需要添加额外的判断：引擎在合成时该方法不能调用
     */
    public void loadOfflineModel(String mode) {
        offlineVoice = mode;
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        int result = synthesizer.loadModel(offlineResource.getModelFilename(), offlineResource.getTextFilename());
        checkResult(result, "loadModel");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

    private void toPrint(String s) {
        Log.i(TAG, s);
    }


    /**
     * 暂停播放。仅调用speak后生效
     */
    public void pause() {
        int result = synthesizer.pause();
        checkResult(result, "pause");
    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
    public void resume() {
        int result = synthesizer.resume();
        checkResult(result, "resume");
    }

    /*
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    public void stop() {
        int result = synthesizer.stop();
        checkResult(result, "stop");
    }

    public void release() {
        Log.i(TAG, "releaseTts");
        synthesizer.release();
    }

    /**
     * 设置发声人
     *
     * @param mode
     */
    public void changeVoiceMode(String mode) {
        changeOnlineVoice(mode);
//        changeOfflineVoice(mode);
    }

    /**
     * 设置在线发声音人
     *
     * @param voiceType 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
     */
    private void changeOnlineVoice(String voiceType) {
        String type = voiceType.equals(OfflineResource.VOICE_FEMALE) ? "0" : voiceType.equals(OfflineResource.VOICE_MALE) ? "1" : "3";
        synthesizer.changeVoice(type);
    }

    /**
     * 设置离线发声音人
     *
     * @param voiceType 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
     */
    private void changeOfflineVoice(String voiceType) {
        OfflineResource offlineResource = createOfflineResource(voiceType);
        int result = synthesizer.loadModel(offlineResource.getModelFilename(), offlineResource.getTextFilename());
        checkResult(result, "loadModel");
    }

    protected void handle(Message msg) {
        switch (msg.what) {
            case INIT_SUCCESS:
                msg.what = PRINT;
                break;
            case PLAY_START:
                if (mListener != null) {
                    mListener.onPlayStart((String) msg.obj);
                }
                break;
            case PLAY_DONE:
                if (mListener != null) {
                    mListener.onPlayDone((String) msg.obj);
                }
                break;
            default:
                break;
        }
    }

    //获取value
    private String getMetaDataFromApp(String key) {
        String value = "";
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(),
                    PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }
}
