package com.sunshine.viewlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 耿其权
 * 2016年6月2日16:16:48
 * 水波显示百分比控件
 */
public class WaveView extends View {

    Point mCentrePoint;
    int mNowHeight = 0;//当前水位
    int mRadius = 0;
    boolean mStart = false;//是否开始
    float mTextSise = 22;//文字大小
    Context mContext;
    int mTranX = 0;//水波平移量
    private Paint mCirclePaint;
    private Paint mOutCirclePaint;
    private Paint mWavePaint;
    private Paint mTextPaint;
    int textHeight;
    int mDevide;

    private boolean isDraw = false;// 控制绘制的开关
    private int mCircleColor = Color.parseColor("#ff6600");//背景内圆颜色
    private int mOutCircleColor = Color.parseColor("#f5e6dc");//背景外圆颜色
    private int mWaveColor = Color.parseColor("#ff944d");//水波颜色
    private int mWaterLevel;// 水目标高度
    private int flowNum = 0;//水目标占百分比这里是整数。
    private int mWaveSpeed = 10;//水波起伏速度
    private int mUpSpeed = 10;//水面上升速度


    /**
     * @param context
     * @param attrs
     */
    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        init(mContext, attrs);
        mStart = true;
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        mContext = context;
        init(mContext, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCanvas(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (firstin) {
            mRadius = (int) (0.5 * getWidth() * 0.92);
            mCentrePoint = new Point(getWidth() / 2, getHeight() / 2);
            mWaterLevel = (int) (2 * mRadius * flowNum / 100f);//算出目标水位高度
        }
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.WaveView);
        mCircleColor = a.getColor(R.styleable.WaveView_CircleBackgroundColor, Color.parseColor("#ff6600"));
        mOutCircleColor = a.getColor(R.styleable.WaveView_outBackgroundColor, Color.parseColor("#eeeeee"));
        mWaveColor = a.getColor(R.styleable.WaveView_WaveColor, Color.parseColor("#ff944d"));
        mTextSise = a.getDimensionPixelSize(R.styleable.WaveView_MidTextSize, 22);
        a.recycle();
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        mOutCirclePaint = new Paint();
        mOutCirclePaint.setColor(mOutCircleColor);
        mOutCirclePaint.setStyle(Paint.Style.FILL);
        mOutCirclePaint.setAntiAlias(true);

        mWavePaint = new Paint();
        mWavePaint.setStrokeWidth(1.0F);
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(1.0F);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSise);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        textHeight = (int) (mTextPaint.descent() - mTextPaint.ascent());
        mWaveSpeed = dp2px(context, 5);
        mUpSpeed = dp2px(context, 1);
        mDevide =dp2px(context, 1);

    }


    boolean firstin = true;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (mWaterLevel > mNowHeight) {
                    mNowHeight = mNowHeight + mUpSpeed;
                }
                if (mStart) {
                    if (mTranX > mRadius) {
                        mTranX = 0;
                    }
                    mTranX = mTranX - mWaveSpeed;
                }
                invalidate();
                sendEmptyMessageDelayed(0, 100);
            }
        }
    };


    private void drawCanvas(Canvas canvas) {
        //画背景圆圈
        canvas.drawCircle(mCentrePoint.x, mCentrePoint.y, mRadius / 0.92f, mOutCirclePaint);
        canvas.drawCircle(mCentrePoint.x, mCentrePoint.y, mRadius, mCirclePaint);
        if (mStart) {
            //计算正弦曲线的路径
            int mH = mCentrePoint.y + mRadius - mNowHeight;
            int left = -mRadius / 2;
            int length = 4 * mRadius;
            Path path2 = new Path();
            path2.moveTo(left, mH);
            int i = left;
            while (i < length) {
                int x = i;
                int y = (int) (Math.sin(2*(x + mTranX)/(float)mRadius) * mRadius / 8);
                path2.lineTo(x, mH + y);
                i = i + mDevide;
            }
            path2.lineTo(length, mH);
            path2.lineTo(length, mCentrePoint.y + mRadius);
            path2.lineTo(0, mCentrePoint.y + mRadius);
            path2.lineTo(0, mH);

            canvas.save();
            //这里与圆形取交集，除去正弦曲线多画的部分
            Path pc = new Path();
            pc.addCircle(mCentrePoint.x, mCentrePoint.y, mRadius, Path.Direction.CCW);
            canvas.clipPath(pc, Region.Op.INTERSECT);
            //xhe  clipPath在android4.0系统crash的原因：canvas开启了硬件加速
            /*if (canvas.isHardwareAccelerated()){
                Log.d("WaveView","canvas 开启了硬件加速");
            }*/
            canvas.drawPath(path2, mWavePaint);
            canvas.restore();
            //绘制文字
            if (flowNum == 0) {
                canvas.drawText("--", mCentrePoint.x, mCentrePoint.y + textHeight / 4, mTextPaint);
            } else {
                canvas.drawText(flowNum + "%", mCentrePoint.x, mCentrePoint.y + textHeight / 4, mTextPaint);
            }

        }
    }

    public boolean start() {
        if (hasSetNum) {
            mStart = true;
            mHandler.sendEmptyMessageDelayed(0, 1000);
            return true;
        }
        return false;
    }

    boolean hasSetNum = false;

    public void setFlowNum(int num) {
        flowNum = num;
        mWaterLevel = (int) (2 * mRadius * flowNum / 100f);//算出目标水位高度
        hasSetNum = true;
    }

    public void setTextSise(float s) {
        mTextSise = s;
        mTextPaint.setTextSize(s);
    }

    //设置水波起伏速度
    public void setWaveSpeed(int speed) {
        mWaveSpeed = speed;
    }

    //设置水面上升速度
    public void setUpSpeed(int speed) {
        mUpSpeed = speed;
    }

    public void setColor(int waveColor, int circleColor, int outcircleColor) {
        mWaveColor = waveColor;
        mCircleColor = circleColor;
        mOutCircleColor = outcircleColor;
        mWavePaint.setColor(mWaveColor);
        mCirclePaint.setColor(mCircleColor);
        mOutCirclePaint.setColor(mOutCircleColor);
    }

    /////恢复状态
//@Override
//protected void onRestoreInstanceState(Parcelable state) {
//    if(!(state instanceof SavedState)) {
//        super.onRestoreInstanceState(state);
//        return;
//    }
//    SavedState savedState=(SavedState)state;
//    super.onRestoreInstanceState(savedState.getSuperState());
//    this.status=savedState.status;
//}
//
//    //保存状态
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        Parcelable superState=super.onSaveInstanceState();
//        SavedState savedState=new SavedState(superState);
//        savedState.status=this.status;
//        return savedState;
//
//    }
//
//    public static class SavedState extends BaseSavedState {
//
//        public  Point mCentrePoint;
//        public   int mNowHeight = 0;//当前水位
//        public  int mRadius = 0;
//        public boolean mStart = false;//是否开始
//
//
//        public int mTranX = 0;//水波平移量
//        public   Paint mCirclePaint;
//        public   Paint mOutCirclePaint;
//        public Paint mWavePaint;
//        public Paint mTextPaint;
//
//        public boolean isDraw = false;// 控制绘制的开关
//
//        public int mWaterLevel;// 水目标高度
//        public int flowNum = 0;//水目标占百分比这里是整数。
//        public int mWaveSpeed = 10;//水波起伏速度
//        public int mUpSpeed = 2;//水面上升速度
//        public SavedState(Parcelable superState) {
//            super(superState);
//            TextView
//        }
//
//        @Override
//        public int describeContents() {
//            return 0;
//        }
//
//        @Override
//        public void writeToParcel(Parcel dest, int flags) {
//            super.writeToParcel(dest,flags);
//            dest.writeByte(this.status ? (byte) 1 : (byte) 0);
//        }
//
//        private SavedState(Parcel in) {
//            super(in);
//            this.status = in.readByte() != 0;
//        }
//
//        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
//            @Override
//            public SavedState createFromParcel(Parcel source) {
//                return new SavedState(source);
//            }
//
//            @Override
//            public SavedState[] newArray(int size) {
//                return new SavedState[size];
//            }
//        };
//    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(0);//销毁
    }
    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}