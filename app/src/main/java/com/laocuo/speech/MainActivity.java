package com.laocuo.speech;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.aip.asrwakeup3.core.BaiduAsr;
import com.baidu.tts.sample.BaiduTts;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BaiduAsr.BaiduAsrInterface {

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
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaiduTts.getInstance().stop();
        BaiduAsr.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaiduTts.getInstance().release();
        BaiduAsr.getInstance().release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tts:
                BaiduTts.getInstance().loadOnlineMode("0");
                BaiduTts.getInstance().speak("离开家第三方老会计");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            BaiduTts.getInstance().loadOnlineMode("1");
                            BaiduTts.getInstance().speak("死垃圾可破解");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.asr:
                BaiduAsr.getInstance().start();
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
                Manifest.permission.CHANGE_WIFI_STATE
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
            BaiduTts.getInstance().setContext(this).initialTts();
            BaiduAsr.getInstance().setContext(this).setBaiduAsrInterface(this).initAsr();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
        BaiduTts.getInstance().setContext(this).initialTts();
        BaiduAsr.getInstance().setContext(this).setBaiduAsrInterface(this).initAsr();
    }

    @Override
    public void onWakeUp() {
        mContent.setText("onWakeUp");
    }

    @Override
    public void onSpeechTake(String voice) {
        mContent.setText(voice);
    }
}
