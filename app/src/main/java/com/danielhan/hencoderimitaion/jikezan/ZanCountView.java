package com.danielhan.hencoderimitaion.jikezan;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.danielhan.hencoderimitaion.R;
import com.danielhan.hencoderimitaion.utils.DensityUtil;

/**
 * @author DanielHan
 * @date 2017/11/15
 */

public class ZanCountView extends View {

    public static final float DEFAULT_TEXT_SIZE = 15;
    public static final int DEFAULT_TEXT_COLOR = 0xffC8C8CC;
    public static final int DEFAULT_COUNT = 0;
    private static final int DEFAULT_ANIM_DURING = 250;
    private static final float DEFAULT_TEXT_ALPHA_MIN = 0f;
    private static final float DEFAULT_TEXT_ALPHA_MAX = 1f;

    private Paint textPaint;
    private int mLeftPadding, mTopPadding, mRightPadding, mBottomPadding;

    private String[] mTexts;//mTexts[0]是不变的部分，mTexts[1]原来的部分，mTexts[2]变化后的部分
    private PointF[] mTextPoints;//表示各部分的坐标
    private float mOldOffsetY;
    private float mNewOffsetY;
    private float textAlpha;
    //文本的上下移动变化值
    private float OFFSET_MIN;
    private float OFFSET_MAX;
    private boolean toBigger;

    //自定义属性
    private float textSize;
    private int textColor = DEFAULT_TEXT_COLOR;
    private int count = DEFAULT_COUNT;

    public ZanCountView(Context context) {
        this(context, null);
    }

    public ZanCountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZanCountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZanCountView);
        textSize = DensityUtil.sp2px(context, DEFAULT_TEXT_SIZE);
        textSize = typedArray.getDimension(R.styleable.ZanCountView_text_size, textSize);
        textColor = typedArray.getColor(R.styleable.ZanCountView_text_color, textColor);
        count = typedArray.getInt(R.styleable.ZanCountView_count, count);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        mLeftPadding = getPaddingLeft();
        mTopPadding = getPaddingTop();
        mRightPadding = getPaddingRight();
        mBottomPadding = getPaddingBottom();


        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);

        mTexts = new String[3];
        mTextPoints = new PointF[3];
        mTextPoints[0] = new PointF();
        mTextPoints[1] = new PointF();
        mTextPoints[2] = new PointF();
        calculateChangeNum(0);
        OFFSET_MIN = 0;
        OFFSET_MAX = 1.5f * textSize;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (int) (mLeftPadding + textPaint.measureText(String.valueOf(count)) + mRightPadding);
        int height = (int) (mTopPadding + textSize + mBottomPadding);
        width = Math.max(width, getSuggestedMinimumWidth());
        height = Math.max(height, getSuggestedMinimumHeight());
        setMeasuredDimension(measureSize(width, widthMeasureSpec), measureSize(height, heightMeasureSpec));
    }

    private int measureSize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//UNSPECIFIED,没有任何限制，所以可以设置任何大小
                //多半出现在自定义的父控件的情况下，期望由自控件自行决定大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: { //1. layout使用的是wrap_content
                //2. layout使用的是match_parent,但父控件使用的是确定的值或者wrap_content
                mySize = Math.min(size, defaultSize);
                break;
            }
            case MeasureSpec.EXACTLY: {//1. layout给出了确定的值，比如：100dp
                //2. layout使用的是match_parent，但父控件的size已经可以确定了，比如设置的是具体的值或者match_parent
                mySize = size;
                break;
            }
        }
        return mySize;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textPaint.setAlpha(255);
        canvas.drawText(String.valueOf(mTexts[0]), mTextPoints[0].x, mTextPoints[0].y, textPaint);
        textPaint.setAlpha((int) (textAlpha * 255));
        canvas.drawText(String.valueOf(mTexts[1]), mTextPoints[1].x, mTextPoints[1].y, textPaint);
        textPaint.setAlpha((int) ((1 - textAlpha) * 255));
        canvas.drawText(String.valueOf(mTexts[2]), mTextPoints[2].x, mTextPoints[2].y, textPaint);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle data = new Bundle();
        data.putParcelable("superData", super.onSaveInstanceState());
        data.putInt("count", count);
        return data;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle data = (Bundle) state;
        Parcelable superData = data.getParcelable("superData");
        super.onRestoreInstanceState(superData);
        count = data.getInt("count", DEFAULT_COUNT);
        init(getContext());
    }

    /**
     * 点赞动画
     */
    private void startAnim(boolean isToBigger) {
        ObjectAnimator textOffsetYAnimator = ObjectAnimator.ofFloat(this, "textOffsetY", OFFSET_MIN, isToBigger ? OFFSET_MAX : -OFFSET_MAX);
        textOffsetYAnimator.setDuration(DEFAULT_ANIM_DURING);
        ObjectAnimator textAlphaAnimator = ObjectAnimator.ofFloat(this, "textAlpha", DEFAULT_TEXT_ALPHA_MAX, DEFAULT_TEXT_ALPHA_MIN);
        textAlphaAnimator.setDuration(DEFAULT_ANIM_DURING);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textOffsetYAnimator, textAlphaAnimator);
        animatorSet.start();
    }

    /**
     * 计算不变，原来，和改变后各部分的数字
     * 这里是只针对加一和减一去计算的算法，因为直接设置的时候没有动画
     */
    public void calculateChangeNum(int change) {
        if (change == 0) {
            mTexts[0] = String.valueOf(count);
            mTexts[1] = "";
            mTexts[2] = "";
            calculateLocation();
            return;
        }
        toBigger = change > 0;
        String oldNum = String.valueOf(count);
        String newNum = String.valueOf(count + change);

        for (int i = 0; i < oldNum.length(); i++) {
            char oldC = oldNum.charAt(i);
            char newC = newNum.charAt(i);
            if (oldC != newC) {
                mTexts[0] = i == 0 ? "" : newNum.substring(0, i);
                mTexts[1] = oldNum.substring(i);
                mTexts[2] = newNum.substring(i);
                break;
            }
        }
        count += change;
        startAnim(change > 0);
    }

    /**
     * 设置文本偏移位置
     *
     * @param offsetY
     */
    public void setTextOffsetY(float offsetY) {
        this.mOldOffsetY = offsetY;//变大是从[0,1]，变小是[0,-1]
        if (toBigger) {//从下到上[-1,0]
            this.mNewOffsetY = offsetY - OFFSET_MAX;
        } else {//从上到下[1,0]
            this.mNewOffsetY = OFFSET_MAX + offsetY;
        }
        calculateLocation();
        invalidate();
    }

    /**
     * 计算三段text的位置
     */
    private void calculateLocation() {
        String text = String.valueOf(count);
        float textWidth = textPaint.measureText(text) / text.length();
        float unChangeWidth = textWidth * mTexts[0].length();

        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        float y = mTopPadding + (textSize - fontMetrics.bottom - fontMetrics.top) / 2;

        mTextPoints[0].x = mLeftPadding;
        mTextPoints[1].x = mLeftPadding + unChangeWidth;
        mTextPoints[2].x = mLeftPadding + unChangeWidth;

        mTextPoints[0].y = y;
        mTextPoints[1].y = y - mOldOffsetY;
        mTextPoints[2].y = y - mNewOffsetY;
    }

    /**
     * 设置text的透明度
     *
     * @param alpha
     */
    private void setTextAlpha(float alpha) {
        this.textAlpha = alpha;
        invalidate();
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setCount(int count) {
        this.count = count;
        calculateChangeNum(0);
        invalidate();
    }
}
