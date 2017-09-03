package com.gftools;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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
//        //进度条半径
//        float radius = (mWidth > mHeight ? mHeight : mWidth) / 2;
//        float scale = radius / 540;
//        if (radius <= 0){
//            //进度条半径为0的时候啥都不画
//            return;
//        }
//        //进度条粗细
//        float lineWidth = 50*scale;
//        float padding = 30*scale;
//        RectF oval = new RectF(mWidth/2-radius+padding , mHeight/2-radius+padding , mWidth/2+radius-padding, mHeight/2+radius-padding);
//        canvas.drawArc(oval , 0 , 360 , false , getPaint(Color.GRAY , Paint.Style.STROKE, lineWidth , 0));
//        canvas.drawArc(oval , 270 , progress*360/100 , false , getPaint(0xffff6521 , Paint.Style.STROKE , lineWidth , 0));
//        StringBuilder sb = new StringBuilder(progress+ "%");
//        while (sb.length() < 4){
//            sb.insert(0 , " ");
//        }
//        float textSize = 300*scale;
//        canvas.drawText(sb.toString() , mWidth/2 - 350*scale , mHeight/2 + 120*scale ,
//                getPaint(0xffff6521 , Paint.Style.FILL, lineWidth , textSize));


        Path path = new Path();
        path.moveTo(10 , 10);
        path.lineTo(100, 100);
        path.lineTo(200 , 10);
        path.lineTo(300 , 100);
        path.lineTo(400 , 10);
        path.lineTo(400 , 200);
        path.lineTo(10 , 200);
        path.lineTo(10 , 10);

        canvas.drawPath(path , getPaint(Color.LTGRAY , Paint.Style.FILL, 1 , 0));
    }

    private ValueAnimator mAnimator;

    public void startAnimation(){
        mAnimator = ValueAnimator.ofFloat(0 , 100);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lv("animate : " + animation.getAnimatedValue());
                setProgress((int)(float)animation.getAnimatedValue());
            }
        });
        mAnimator.setRepeatCount(-1);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setDuration(5000);
        mAnimator.start();
    }
}
