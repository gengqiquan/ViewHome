package com.sunshine.viewlibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by gengqiquan on 2017/6/14.
 */

public class FlashLoadingView extends View {
    static final int PROGRESS_WHAT = 0X000;
    static final int MOVE_DOTS_WHAT = 0X111;
    Context mContext;
    int mWidth, mHeight;
    float mProgress = 0;
    int minDistance = 5;
    int maxRadius = 20;
    Point mCenter;
    CopyOnWriteArrayList<Dot> mDots = new CopyOnWriteArrayList<>();
    Paint mPaint = new Paint();

    LinkedList<Dot> mQueue = new LinkedList<>();

    Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            while (true) {
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!mQueue.isEmpty())
                    synchronized (mQueue) {
                        mDots.add(mQueue.removeFirst());
                    }
                if (mProgress < targetProgress) {
                    mProgress = mProgress + 0.2f;
                }
                mCenter.x = (int) ((mProgress / 100) * mWidth) + 20;//在进度条前方20距离位置汇聚光点
                moveDots();
                particleSystem.update(0);
//                particleSystem.update(new Random().nextFloat() * 0.18f);
                mHandler.sendEmptyMessage(MOVE_DOTS_WHAT);
            }
        }
    });

    private void moveDots() {
        List<Dot> removeList = new ArrayList<>();
        synchronized (mDots) {
            for (Dot dot : mDots) {
                dot.move();
                if (dot.nearTarger()) {
                    removeList.add(dot);
                }
            }
            Log.d("mDots.size=", mDots.size() + "");

            mDots.removeAll(removeList);
        }
        Log.d("remove.size=", mDots.size() + "");
    }

    int random(int from, int to) {
        return mRandom.nextInt(to) + from;
    }

    Random mRandom = new Random();
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            mDots.add((Dot) msg.obj);

            switch (msg.what) {
                case PROGRESS_WHAT:
                    targetProgress = msg.arg1;
                    break;
                case MOVE_DOTS_WHAT:
                    invalidate();
                    break;
            }
//            invalidate(new Rect());
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Dot dot : mDots) {
            mPaint.setColor(dot.color);
            if (dot.nearTarger()) {
                continue;
            }
            canvas.drawCircle(dot.point.x, dot.point.y, dot.radius, mPaint);
        }

        @SuppressLint("DrawAllocation") Path p = new Path();
        p.addRect(0, mCenter.y - 10, mWidth, mCenter.y + 10, Path.Direction.CCW);

        @SuppressLint("DrawAllocation") Path path = new Path();
        path.addRect(0, mCenter.y - 40, (mProgress / 100f) * mWidth, mCenter.y + 40, Path.Direction.CCW);
        canvas.save();
        canvas.clipPath(path, Region.Op.INTERSECT);
        mPaint.setColor(Color.parseColor("#D98719"));
        canvas.drawPath(p, mPaint);
        canvas.restore();
        canvas.save();
        canvas.translate(mCenter.x - 30, 0);
        if (targetProgress > 0) {
            particleSystem.onDraw(canvas);
        }
        canvas.restore();
    }

    public void createDots(int num, float nowX) {

        List<Dot> list = new ArrayList<>();
        for (int i = 0; i < num * 2; i++) {
            Point target = new Point((int) ((nowX / 100f) * mWidth + i), mCenter.y);
            Dot dot = new Dot(target);
            // ALTER: 2017/6/14  可以修改成从进度条进度所在位置的某个半径范围内生成
            Point p = new Point(random(0, mWidth), random(0, mHeight));
            dot.point = p;
            dot.radius = random(10, maxRadius);
            dot.color = Color.parseColor("#D98719");
            list.add(dot);
            synchronized (mQueue) {
                mQueue.addLast(dot);
            }
        }
//        synchronized (mQueue) {
//            mQueue.addFirst(list);
//        }
    }

    int targetProgress = 0;

    public void progress(int p) {
        if (p > 100) {
            return;
        }
        createDots((int) (p - mProgress), mProgress);
        Message msg = mHandler.obtainMessage();
        msg.what = PROGRESS_WHAT;
        msg.arg1 = p;
        mHandler.sendMessageDelayed(msg, 1000);
    }


    public FlashLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlashLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    ParticleSystem particleSystem;

    private void init() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidth = outMetrics.widthPixels;
        mHeight = outMetrics.heightPixels;
        mCenter = new Point(mWidth / 2, mHeight / 2);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        particleSystem = new ParticleSystem(10, mCenter.y, mCenter.y - 60, 30);
//        particleSystem = new ParticleSystem(10, 500, 300, 100);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mThread.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mThread.stop();
    }

    //Point mCenter;

    class Dot {
        Point point;
        // Point mCenter;
        float radius;
        int color;
        double scale = 0.01;

        public Dot(Point mCenter) {
            // this.mCenter = mCenter;
        }

        void move() {
            if (Math.abs(point.x - mCenter.x) >= minDistance) {
                point.x = (int) (point.x - (point.x - mCenter.x) * scale);
            } else {
//                if (radius <= minDistance) {
//                    point.x = mCenter.x;
//                }
                if (Math.abs(point.x - mCenter.x) != 0) {
                    point.x = point.x + ((point.x < mCenter.x) ? 1 : -1);
                    if (radius > 0)
                        radius = radius - 1;
                }
            }
            // TODO: 2017/6/14 靠近 mCenter.x轴位置的点 值直接变成mCenter.x
            point.y = (int) (point.y - (point.y - mCenter.y) * scale);
            scale = scale + 0.001;
            if (radius > 3) {
                radius = radius * 0.99f;
            }


            if (Math.abs(point.y - mCenter.y) != 0 && Math.abs(point.y - mCenter.y) <= minDistance) {
                point.y = point.y + ((point.y < mCenter.y) ? 1 : -1);
                if (radius > 0)
                    radius = radius - 1;
            }
//            if (radius < 0) {
//                radius = 0;
//            }
        }

        boolean nearTarger() {
            if (Math.pow(Math.abs(point.x - mCenter.x), 2) + Math.pow(Math.abs(point.y - mCenter.y), 2) < Math.pow(radius, 2)) {
                return true;
            }
            if (radius < 1) {
                return true;
            }
//            if (Math.abs(point.y - mCenter.y) < radius) {
//                return true;
//            }
            return false;
        }
    }
}
