package com.gftools;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import static com.gftools.utils.UtilsKt.lv;

/**
 * Created by FH on 2017/9/3.
 */

public class RoundnessProgressBar extends View{
    Paint mPaint;
    private int mHeight;
    private int mWidth;
    private int progress = 50;
    private int horizentalShift = 0;
    private int horizentalShift2 = 0;

    public RoundnessProgressBar(Context context) {
        super(context);
        init();
    }

    public RoundnessProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundnessProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RoundnessProgressBar setProgress(int progress) {
        if (progress >= 0 && this.progress != progress){
            this.progress = progress;
            postInvalidate();
        }
        return this;
    }

    public RoundnessProgressBar setHorizentalShift(int horizentalShift) {
        if (this.horizentalShift != horizentalShift){
            this.horizentalShift = horizentalShift;
            postInvalidate();
        }
        return this;
    }

    private Paint getPaint (int color , Paint.Style style , float strokeWidth , float textSize){
        if (mPaint == null){
            mPaint = new Paint();
        }
        mPaint.setColor(color);
        mPaint.setStyle(style);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        return mPaint;
    }
    private void init(){
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                test();
//            }
//        });
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int measuredHeight, measuredWidth;

        if (widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSize;
        } else {
            measuredWidth = 15;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = heightSize;
        } else {
            measuredHeight = 15;
        }
        lv("onMeasure measuredWidth = " + measuredWidth + "  measuredHeight " + measuredHeight);
        setMeasuredDimension(measuredWidth , measuredHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        startAnimation();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        Log.v("FH" , "onLayout mWidth " + mWidth + " mHeight " + mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.v("FH" , "onDraw");
        //进度条半径
        float radius = (mWidth > mHeight ? mHeight : mWidth) / 2;
        if (radius <= 0){
            //进度条半径为0的时候啥都不画
            return;
        }
        //圆心
        float centerX = mWidth/2;
        float centerY = mHeight/2;
        if (progress != 100){
            //画底圆
            canvas.drawCircle(centerX , centerY , radius , getPaint(0x99cccccc, Paint.Style.FILL , 3 , 0));
        }
        else {
            //进度为100%时直接画满圆,其余的都不画
            canvas.drawCircle(centerX , centerY , radius , getPaint(0xffff7f50, Paint.Style.FILL , 3 , 0));
            return;
        }
        Path path = new Path();
        //先画波浪线
        //起始点坐标
        float fromY = (centerY + radius) - progress*radius/50;
        float fromX = (float) (centerX - Math.sqrt(Math.pow(radius, 2) - Math.pow(fromY - centerY, 2)));
        path.moveTo(fromX , fromY);
        //终点坐标
        float toY = fromY;
        float toX = 2 * centerX - fromX;
        //波浪幅度,根据当前波浪线宽度进行缩放
        float fudu = 200 * (toX-fromX)/ 1080;
        //波浪线需要画3阶赛贝尔曲线,需要起点,终点,一号参考点(波谷参考点),二号参考点(波峰参考点)
        //波浪线的参考点的x位置是由左向右移动的,以此模拟破浪的移动
        //计算波谷参考点的x位置
        float point1X = fromX + horizentalShift * (toX - fromX) / 100;
        //计算波峰参考点的x位置
        float point2X = fromX + horizentalShift2 * (toX - fromX) / 100;

        //计算一号参考点最大能距离中轴(fromY)达到什么位置.
        // 因为考虑到有可能会超出底圆的范围.所以要计算这个,要把一号参考点的Y值规定在底圆的范围内.
        //原理是用一号参考点的x计算与底圆的上下两个交点,Y值保持在这两个交点的Y值之间就行了.
        float maxPoint1Y = (float) (Math.sqrt(radius*radius-(point1X-centerX)*(point1X-centerX)) + centerY);
        float minPoint1Y = (float) (centerY - Math.sqrt(radius*radius-(point1X-centerX)*(point1X-centerX)));
        if (maxPoint1Y < minPoint1Y){
            float temp = maxPoint1Y;
            maxPoint1Y = minPoint1Y;
            minPoint1Y = temp;
        }
        //根据计算出的Y值的最大最小值限定一号参考点的Y值
        float point1Y = (fromY + fudu) > maxPoint1Y ? maxPoint1Y : (fromY + fudu);

        //对靠近底圆左右边缘的一号点的Y值,这里需要做一个衰减,越靠近底圆左右边缘的1号点位置,Y值偏离中轴(fromY)的距离需要越接近0
        //主要是防止一号点从圆右返回圆左的时候不至于波浪线瞬间抖动过大
        if (horizentalShift > 90 || horizentalShift < 10){
            int tempInt = horizentalShift-50;
            tempInt = Math.max(tempInt , -tempInt);
            point1Y = fromY + (point1Y - fromY)*(50-tempInt)/10;
        }

        //2号点原理与1号点一致,唯一不同的是2号点是波峰点
        float maxPoint2Y = (float) (Math.sqrt(radius*radius-(point2X-centerX)*(point2X-centerX)) + centerY);
        float minPoint2Y = (float) (centerY - Math.sqrt(radius*radius-(point2X-centerX)*(point2X-centerX)));
        if (maxPoint2Y < minPoint2Y){
            float temp = maxPoint2Y;
            maxPoint2Y = minPoint2Y;
            minPoint2Y = temp;
        }
        float point2Y = (fromY - fudu) < minPoint2Y ? minPoint2Y : (fromY - fudu);

        if (horizentalShift2 > 90 || horizentalShift2 < 10){
            int tempInt = horizentalShift2-50;
            tempInt = Math.max(tempInt , -tempInt);
            point2Y = fromY - (fromY - point2Y)*(50-tempInt)/10;
        }

        //由于两个点在由左向右到头之后会回到最左,这样循环,可能会导致1,2号点的先后顺序倒转,这时也需要倒转1号点和2号点的绘制顺序
        if (point1X < point2X){
            path.cubicTo(point1X , point1Y , point2X , point2Y , toX , toY);
        }
        else {
            path.cubicTo(point2X , point2Y , point1X , point1Y , toX , toY);
        }

        //画下方的圆弧
        float tempA , tempB;
        tempA = mWidth - 2*fromX;//弦长
        //使用余弦定理计算出内切三角形的圆心角的度数
        tempB = (radius*radius*2 - tempA*tempA)/(2*radius*radius);
        double alpha = Math.acos(tempB)*180/Math.PI;
        //画弧,需要区分内切三角形在圆的上方还是下方
        if (progress <= 50){
            path.arcTo(centerX - radius , centerY - radius , centerX + radius , centerY + radius , (float)(180-alpha)/2 , (float)alpha, false);
        }
        else if (progress < 100){
            path.arcTo(centerX - radius , centerY - radius , centerX + radius , centerY + radius , (float)(180-alpha)/(-2) , (float) (360-alpha), false);
        }
        //会原点,封闭path
        path.lineTo(fromX , fromY);
        canvas.drawPath(path , getPaint(0xffff7f50, Paint.Style.FILL , 0 , 0));

        //写字
        float scale = radius / 540;
        StringBuilder sb = new StringBuilder(progress+ "%");
        while (sb.length() < 4){
            sb.insert(0 , " ");
        }
        float textSize = 300*scale;
        canvas.drawText(sb.toString() , mWidth/2 - 350*scale , mHeight/2 + 120*scale ,
                getPaint(0xff777777 , Paint.Style.FILL, 0 , textSize));
    }

    private ValueAnimator mAnimator;
    private ValueAnimator mAnimator2;

    public void startAnimation(){
        //一号参考点向前运动的animator
        if (mAnimator == null){
            mAnimator = ValueAnimator.ofInt(0 , 100);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    lv("animate : " + value);
                    setHorizentalShift(value);
                    //一号参考点向前运动到一定距离后再启动2号参考点的运动
                    if (value > 30){
                        if (!mAnimator2.isStarted()){
                            mAnimator2.start();
                        }
                    }
                }
            });
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimator.setDuration(3000);
        }
        mAnimator.start();
        //二号参考点向前运动的animator
        if (mAnimator2 == null){
            mAnimator2 = ValueAnimator.ofInt(0 , 100);
            mAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    lv("animate : " + value);
                    horizentalShift2 = value;
                }
            });
            mAnimator2.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator2.setRepeatMode(ValueAnimator.RESTART);
            mAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimator2.setDuration(3000);
        }
    }

    public void test(){
        ValueAnimator testAnimator = ValueAnimator.ofInt(0, 100);
        testAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                lv("animate : " + value);
                setProgress(value);
            }
        });
        testAnimator.setRepeatCount(ValueAnimator.INFINITE);
        testAnimator.setRepeatMode(ValueAnimator.RESTART);
        testAnimator.setInterpolator(new LinearInterpolator());
        testAnimator.setDuration(10000);
        testAnimator.start();
    }
}
