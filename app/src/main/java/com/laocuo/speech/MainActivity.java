package com.laocuo.speech;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.speech.SpeechHelper;
import com.baidu.speech.SpeechListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SpeechListener {

    private Button mTTS, mASR;

    private TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTTS = findViewById(R.id.tts);
        mASR = findViewById(R.id.asr);
        mContent = findViewById(R.id.asr_content);
        mTTS.setOnClickListener(this);
        mASR.setOnClickListener(this);
        initPermission();
        SpeechHelper.getInstance().setSpeechListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpeechHelper.getInstance().stopTts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpeechHelper.getInstance().release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tts:
                SpeechHelper.getInstance().speak("百度语音测试");
                break;
            case R.id.asr:
                SpeechHelper.getInstance().startWakeUp();
                break;
            default:
                break;
        }
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.RECORD_AUDIO
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        } else {
            initSpeech();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        initSpeech();
    }

    private void initSpeech() {
        SpeechHelper.getInstance().setContext(MainActivity.this).init();
    }

    @Override
    public void recogFinish(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContent.setText(result);
            }
        });
    }
}
