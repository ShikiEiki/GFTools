package com.gftools;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import static com.gftools.utils.UtilsKt.*;

/**
 * Created by FH on 2017/9/3.
 */

public class CircleProgressBar extends View{
    Paint mPaint;
    private int mHeight;
    private int mWidth;
    private int progress = 30;


    public CircleProgressBar(Context context) {
        super(context);
        init();
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CircleProgressBar setProgress(int progress) {
        if (progress >= 0){
            this.progress = progress;
        }
        postInvalidate();
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
        //测试进度条走动
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });
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
        //这个缩放值是调试调出来的
        float scale = radius / 540;
        if (radius <= 0){
            //进度条半径为0的时候啥都不画
            return;
        }
        //进度条粗细
        float lineWidth = 50*scale;
        //由于圆线条有粗细,所以需要留够一定的padding值,否则圆线条会超出控件
        float padding = 30*scale;
        RectF oval = new RectF(mWidth/2-radius+padding , mHeight/2-radius+padding , mWidth/2+radius-padding, mHeight/2+radius-padding);
        //先画底层圆
        canvas.drawArc(oval , 0 , 360 , false , getPaint(Color.GRAY , Paint.Style.STROKE, lineWidth , 0));
        //再画实际进度圆弧
        canvas.drawArc(oval , 270 , progress*360/100 , false , getPaint(0xffff6521 , Paint.Style.STROKE , lineWidth , 0));
        StringBuilder sb = new StringBuilder(progress+ "%");
        //写字,如果字符少于4个,添加空格至4个字符
        while (sb.length() < 4){
            sb.insert(0 , " ");
        }
        float textSize = 300*scale;
        canvas.drawText(sb.toString() , mWidth/2 - 350*scale , mHeight/2 + 120*scale ,
                getPaint(0xffff6521 , Paint.Style.FILL, lineWidth , textSize));

    }

    private ValueAnimator mAnimator;

    public void startAnimation(){
        mAnimator = ValueAnimator.ofInt(0 , 100);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                lv("animate : " + value);
                setProgress(value);
            }
        });
        mAnimator.setRepeatCount(-1);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(5000);
        mAnimator.start();
    }
}
