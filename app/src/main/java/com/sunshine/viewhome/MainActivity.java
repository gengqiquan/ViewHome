package com.sunshine.viewhome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sunshine.viewlibrary.FlashLoadingView;
import com.sunshine.viewlibrary.WaveView;

public class MainActivity extends AppCompatActivity {
    int p = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FlashLoadingView mWave1 = (FlashLoadingView) findViewById(R.id.flash);
        //xhe 关闭硬件加速，使用软件加速 防止自定义WaveView中clipPath发生crash
        mWave1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (p < 100) {
                    mWave1.progress(p);
                    p = p + 10;
                }
            }
        });
    }
}
