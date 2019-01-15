package com.sunshine.viewhome;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunshine.viewlibrary.FlashLoadingView;
import com.sunshine.viewlibrary.WaveView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> list = new ArrayList<>();
    View tv;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        tv = findViewById(R.id.tv_button);
        FrameLayout content = (FrameLayout) findViewById(android.R.id.content);
//        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                log("scroll");
//            }
//        });
//        tv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                log("onLayoutChange");
//                Log.e("onLayout", ":" + top);
//            }
//        });
//        tv.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
//            @Override
//            public void onDraw() {
//                log("onDraw");
//            }
//        });
//        tv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                log("onGlobalLayout");
//            }
//        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击了", Toast.LENGTH_SHORT).show();
            }
        });
        int[] outLocation = new int[2];

        tv.getLocationOnScreen(outLocation);
        final View f = new View(this);
        f.setBackgroundColor(Color.parseColor("#ff6600"));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(80, 80);
        lp.setMargins(outLocation[0], outLocation[1], 0, 0);
        content.addView(f, lp);
        tv.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
//                log("onScrollChanged");

            }
        });
        tv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                bind(f);
                return true;
            }
        });

    }

    void bind(View f) {
        int[] outLocation = new int[2];
        tv.getLocationOnScreen(outLocation);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) f.getLayoutParams();
        lp.setMargins(outLocation[0], outLocation[1], 0, 0);
        f.setLayoutParams(lp);
//        str += "  PivotY:" + tv.getPivotY();
//        str += "  ScrollY:" + tv.getScrollY();
//        str += "  TranslationY:" + tv.getTranslationY();
        tv.getLocationOnScreen(outLocation);
        String str = "" + outLocation[1];

        Log.e("  outLocationY:", str);
    }

    void log(String key) {
        String str = "  y:" + tv.getY();
//        str += "  PivotY:" + tv.getPivotY();
//        str += "  ScrollY:" + tv.getScrollY();
//        str += "  TranslationY:" + tv.getTranslationY();
        int[] outLocation = new int[2];
        tv.getLocationOnScreen(outLocation);
        str += "  outLocationY:" + outLocation[1];

        Log.e(key, str);
    }
}
