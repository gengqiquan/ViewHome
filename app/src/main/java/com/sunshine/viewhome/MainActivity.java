package com.sunshine.viewhome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sunshine.viewlibrary.WaveView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WaveView     mWave1 = (WaveView) findViewById(R.id.wave);
        //xhe 关闭硬件加速，使用软件加速 防止自定义WaveView中clipPath发生crash
        mWave1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWave1.setFlowNum(50);
        mWave1.start();
    }
}
