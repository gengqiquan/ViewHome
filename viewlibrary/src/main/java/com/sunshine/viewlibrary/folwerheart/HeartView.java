package com.sunshine.viewlibrary.folwerheart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Package com.hc.testheart
 * Created by HuaChao on 2016/5/25.
 */
public class HeartView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder surfaceHolder;
    int offsetX;
    int offsetY;
    private Garden garden;
    private int width;
    private int height;
    private Paint backgroundPaint;
    private boolean isDrawing = false;
    private Bitmap bm;
    private Canvas canvas;
    private int heartRadio = 1;

    public HeartView(Context context) {
        super(context);
        init();
    }

    public HeartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        garden = new Garden();
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(50, 249, 204, 226));
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.STROKE);

    }

    List<Bloom> blooms = new ArrayList<>();

    public Point getHeartPoint(float angle) {
        float t = (float) (angle / Math.PI);
        float x = (float) (heartRadio * (16 * Math.pow(Math.sin(t), 3)));
        float y = (float) (-heartRadio * (13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t)));

        return new Point(offsetX + (int) x, offsetY + (int) y);
    }


    //绘制列表里所有的花朵
    private void drawHeart() {

        canvas.drawRect(0, 0, width, height, backgroundPaint);
        for (Bloom b : blooms) {
            b.draw(canvas);
        }

        Canvas c = surfaceHolder.lockCanvas();

        c.drawBitmap(bm, 0, 0, null);

        surfaceHolder.unlockCanvasAndPost(c);

    }

    Paint textPaint;

    public void reDraw() {
        blooms.clear();


        drawOnNewThread();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

    }

    //开启一个新线程绘制
    private void drawOnNewThread() {
        new Thread() {
            @Override
            public void run() {
                if (isDrawing) return;
                isDrawing = true;

                float angle1 = 20;
                float angle2 = 20;
                while (true) {

                    Bloom bloom1 = getBloom(angle1);
                    if (bloom1 != null) {
                        blooms.add(bloom1);
                    }
                    Bloom bloom2 = getBloom(angle2);
                    if (bloom2 != null) {
                        blooms.add(bloom2);
                    }
                    if (angle1 <= 10) {
                        Looper.prepare();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                mLengthAnimator.start();
                            }
                        });
                        break;
                    } else {
                        angle1 -= 0.2;
                    }
                    if (angle2 >= 30) {
                        break;
                    } else {
                        angle2 += 0.2;
                    }
                    drawHeart();
                    try {
                        sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isDrawing = false;
            }
        }.start();
    }

    float mLength = 0;
    ValueAnimator mLengthAnimator;

    private Bloom getBloom(float angle) {

        Point p = getHeartPoint(angle);

        boolean draw = true;
        /**循环比较新的坐标位置是否可以创建花朵,
         * 为了防止花朵太密集
         * */
        for (int i = 0; i < blooms.size(); i++) {

            Bloom b = blooms.get(i);
            Point bp = b.getPoint();
            float distance = (float) Math.sqrt(Math.pow(p.x - bp.x, 2) + Math.pow(p.y - bp.y, 2));
            if (distance < Garden.Options.maxBloomRadius * 1.5) {
                draw = false;
                break;
            }
        }
        //如果位置间距满足要求，就在该位置创建花朵并将花朵放入列表
        if (draw) {
            Bloom bloom = garden.createRandomBloom(p.x, p.y);
            return bloom;
        }
        return null;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        this.width = width;
        this.height = height;
        //我的手机宽度像素是1080，发现参数设置为30比较合适，这里根据不同的宽度动态调整参数
        heartRadio = width * 30 / 1080;

        offsetX = width / 2;
        offsetY = height / 2 - 55;
        mLengthAnimator = ValueAnimator.ofFloat(0f, width * 0.4f);
        mLengthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLength = (float) animation.getAnimatedValue();

                canvas.drawText("Love you", offsetX, offsetY, textPaint);
                Canvas c = surfaceHolder.lockCanvas();

                c.drawBitmap(bm, 0, 0, null);

                surfaceHolder.unlockCanvasAndPost(c);
            }
        });
        bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(bm);

        drawOnNewThread();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}