package com.sunshine.viewlibrary;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * gengqiquan
 * 2016年6月6日10:46:58
 * 仿ios开关控件
 */
public class CoolSwich extends View {

    Point mCirclePoint;
    Point mRightPoint;
    int mRadius = 0;
    int mLength = 0;
    Context mContext;
    int mTranX = 0;//水平平移量
    private Paint mCirclePaint;
    private Paint mOutCirclePaint;
    private Paint mBackPaint;
    private float scale = 0f;
    private boolean isDraw = false;// 控制绘制的开关
    private int mCircleColor = Color.parseColor("#ffffff");//背景内圆颜色
    private int mBackColor = Color.parseColor("#03E17A");//背景外圆颜色
    private int mOutCircleColor = Color.parseColor("#dddddd");//水波颜色
    private boolean isON = false;
    final private int OFF = 0;
    final private int ON = 1;

    /**
     * @param context
     */
    public CoolSwich(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
        init(mContext);
    }

    /**
     * @param context
     * @param attrs
     */
    public CoolSwich(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        init(mContext);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CoolSwich(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        mContext = context;
        init(mContext);
    }

    public boolean isON() {
        return isON;
    }

    private void init(Context context) {
        mContext = context;

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        mOutCirclePaint = new Paint();
        mOutCirclePaint.setColor(mOutCircleColor);
        mOutCirclePaint.setStyle(Paint.Style.FILL);
        mOutCirclePaint.setAntiAlias(true);

        mBackPaint = new Paint();
        mBackPaint.setStrokeWidth(1.0F);
        mBackPaint.setColor(mBackColor);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setAntiAlias(true);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDraw) {
                    if (isON) {
                        off();
                    } else {
                        on();
                    }
                }
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF backRectF = new RectF(mCirclePoint.x - mRadius, 0, mCirclePoint.x + mLength + mRadius, 2 * mRadius);
        canvas.drawRoundRect(backRectF, mRadius, mRadius, mBackPaint);

        if (isON) {//关闭画外圈描边，开启不画
            RectF circleRectF = new RectF(mRightPoint.x - (mLength + mRadius) * (1 - scale), mRadius - mRadius * (1 - scale), mRightPoint.x + mRadius * (1 - scale), mRadius * (1 - scale) + mRadius);
            canvas.drawRoundRect(circleRectF, mRadius, mRadius, mCirclePaint);

            canvas.drawCircle(mCirclePoint.x + mTranX, mCirclePoint.y, mRadius * 0.95f, mCirclePaint);
        } else {
            RectF outRectF = new RectF(mRightPoint.x - (mLength + mRadius) * (1 - scale), mRadius - mRadius * (1 - scale), mRightPoint.x + mRadius * (1 - scale), mRadius * (1 - scale) + mRadius);
            canvas.drawRoundRect(outRectF, mRadius, mRadius, mOutCirclePaint);

            RectF circleRectF = new RectF(mRightPoint.x - (mLength + mRadius) * 0.95f * (1 - scale), mRadius - mRadius * (1 - scale) * 0.95f, mRightPoint.x + mRadius * (1 - scale) * 0.95f, mRadius * (1 - scale) * 0.95f + mRadius);
            canvas.drawRoundRect(circleRectF, mRadius, mRadius, mCirclePaint);

            canvas.drawCircle(mCirclePoint.x + mTranX, mCirclePoint.y, mRadius, mOutCirclePaint);
            canvas.drawCircle(mCirclePoint.x + mTranX, mCirclePoint.y, mRadius * 0.95f, mCirclePaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getWidth();
        int h = getHeight();
        mRadius = h / 2;
        mLength = w - 2 * mRadius;
        mCirclePoint = new Point(mRadius, mRadius);
        mRightPoint = new Point(mRadius + mLength, mRadius);
        if (isON) {
            mTranX = mLength;
        } else {
            mTranX = 0;
        }
    }

    public void off() {
        isDraw = true;
        isON = false;
        mHandler.sendEmptyMessageDelayed(OFF, 50);
        if (mOnSelectChangeListener != null) {
            mOnSelectChangeListener.change(false);
        }
    }

    public void init(boolean b) {
        //isDraw = true;
        if (b) {
            isON = true;
            mTranX = mLength;
            scale=1;
        } else {
            isON = false;
            mTranX = 0;
            scale=0;
        }
        invalidate();
    }

    public void on() {
        isDraw = true;
        isON = true;
        mHandler.sendEmptyMessageDelayed(ON, 50);
        if (mOnSelectChangeListener != null) {
            mOnSelectChangeListener.change(true);
        }

    }


    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OFF:
                    mTranX = mTranX - 10;
                    scale = mTranX / (float) mLength;
                    if (mTranX > 0) {
                        invalidate();
                        this.sendEmptyMessageDelayed(OFF, 20);
                    } else if (mTranX > -10) {
                        mTranX = 0;
                        scale = 0;
                        invalidate();
                        isDraw = false;
                    } else {
                        isDraw = false;

                    }
                    break;
                case ON:
                    mTranX = mTranX + 10;
                    scale = mTranX / (float) mLength;
                    if (mTranX < mLength) {
                        invalidate();
                        this.sendEmptyMessageDelayed(ON, 20);
                    } else if (mTranX < mLength + 10) {
                        mTranX = mLength;
                        scale = 1f;
                        invalidate();
                        isDraw = false;
                    } else {
                        isDraw = false;
                    }

                    break;
            }
        }

    };

    /*
    背景颜色，圆圈颜色，关闭时外圈描边的颜色
     */
    public void setColor(int backColor, int circleColor, int outcircleColor) {
        mBackColor = backColor;
        mCircleColor = circleColor;
        mOutCircleColor = outcircleColor;
        mBackPaint.setColor(backColor);
        mCirclePaint.setColor(mCircleColor);
        mOutCirclePaint.setColor(mOutCircleColor);
    }

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        mOnSelectChangeListener = onSelectChangeListener;
    }

    public interface OnSelectChangeListener {
        void change(boolean on);
    }

    OnSelectChangeListener mOnSelectChangeListener;

}