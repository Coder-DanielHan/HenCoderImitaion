package com.danielhan.hencoderimitaion.jikezan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.danielhan.hencoderimitaion.R;
import com.danielhan.hencoderimitaion.utils.DensityUtil;

/**
 * 点赞图标View
 *
 * @author DanielHan
 * @date 2017/11/14
 */

public class ZanIconView extends View {

    //圆默认值
    public static final int DEFAULT_CIRCLE_COLOR = 0xaee24d3d;
    public static final float DEFAULT_CIRCLE_STROKE_WIDTH = 2f;
    public static final float DEFAULT_CIRCLE_RADIUS_SPACING = 6f;

    //动画默认值
    private static final float DEFAULT_THUMB_SCALE_MIN = 0.9f;
    private static final float DEFAULT_THUMB_SCALE_NORMAL = 1f;
    private static final float DEFAULT_THUMB_SCALE_MAX = 1.1f;
    private static final float DEFAULT_SHINING_SCALE_MIN = 0f;
    private static final float DEFAULT_SHINING_SCALE_NORMAL = 1f;
    //缩放动画的时间
    private static final int DEFAULT_SCALE_DURING = 100;
    //圆圈扩散动画的时间
    private static final int DEFAULT_RADIUS_DURING = 200;

    public static final int DEFAULT_UNZAN_RES_ID = R.drawable.ic_messages_like_unselected;
    public static final int DEFAULT_ZAN_RES_ID = R.drawable.ic_messages_like_selected;


    private Paint bitmapPaint, circlePaint;
    private int mLeftPadding, mTopPadding, mRightPadding, mBottomPadding;
    private Bitmap unZanBitmap, zanBitmap, shiningBitmap;
    private float thumbWidth, thumbHeight, shiningWidth, shiningHeight;
    private float thumbCenterx, thumbCenterY;
    private int shiningStartX, shiningStartY, shiningCenterX, shiningCenterY;

    private float minRadius, maxRadius, radius;
    private float thumbScale = DEFAULT_THUMB_SCALE_NORMAL;
    private float shiningScale = DEFAULT_THUMB_SCALE_NORMAL;

    private boolean isChecked = false;
    private boolean isAnimationFinished;

    /**
     * 自定义属性
     */
    private int unZanResId = DEFAULT_UNZAN_RES_ID;
    private int zanResId = DEFAULT_ZAN_RES_ID;
    private int circleColor = DEFAULT_CIRCLE_COLOR;
    private float circleStrokeWidth;
    //圆环半径差
    private float circleRadiusSpacing;

    public ZanIconView(Context context) {
        this(context, null);
    }


    public ZanIconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ZanIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZanIconView);
        unZanResId = typedArray.getResourceId(R.styleable.ZanIconView_icon_unselected, unZanResId);
        zanResId = typedArray.getResourceId(R.styleable.ZanIconView_icon_selected, zanResId);
        circleColor = typedArray.getColor(R.styleable.ZanIconView_circle_color, circleColor);
        circleStrokeWidth = DensityUtil.dip2px(context, DEFAULT_CIRCLE_STROKE_WIDTH);
        circleStrokeWidth = typedArray.getDimension(R.styleable.ZanIconView_circle_stroke_width, circleStrokeWidth);
        circleRadiusSpacing = DensityUtil.dip2px(context, DEFAULT_CIRCLE_RADIUS_SPACING);
        circleRadiusSpacing = typedArray.getDimension(R.styleable.ZanIconView_circle_radius_spacing, circleRadiusSpacing);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        unZanBitmap = BitmapFactory.decodeResource(getResources(), unZanResId);
        zanBitmap = BitmapFactory.decodeResource(getResources(), zanResId);
        shiningBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected_shining);

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(circleStrokeWidth);

        initSize(context);

    }

    private void initSize(Context context) {
        thumbWidth = zanBitmap.getWidth();
        thumbHeight = zanBitmap.getHeight();
        shiningWidth = shiningBitmap.getWidth();
        shiningHeight = shiningBitmap.getHeight();

        mLeftPadding = getPaddingLeft();
        mTopPadding = getPaddingTop();
        mRightPadding = getPaddingRight();
        mBottomPadding = getPaddingBottom();

        minRadius = Math.max(thumbWidth, thumbHeight) / 2;
        maxRadius = minRadius + circleRadiusSpacing;

        shiningStartX = mLeftPadding + DensityUtil.dip2px(getContext(), 2);
        shiningStartY = mTopPadding - DensityUtil.dip2px(getContext(), 8);
        shiningCenterX = shiningStartX + shiningBitmap.getWidth() / 2;
        shiningCenterY = shiningStartY + shiningBitmap.getHeight() / 2;

        thumbCenterx = mLeftPadding + zanBitmap.getWidth() / 2;
        thumbCenterY = mTopPadding + zanBitmap.getHeight() / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (int) (mLeftPadding + thumbWidth + mRightPadding);
        int height = (int) (mTopPadding + thumbHeight + mBottomPadding);
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
        Bitmap bitmap = (isChecked ? zanBitmap : unZanBitmap);


        //draw circle and shining
        if (isChecked) {
            circlePaint.setColor(isAnimationFinished ? 0x00000000 : circleColor);
            canvas.drawCircle(thumbCenterx, thumbCenterY, radius, circlePaint);
            canvas.save();
            canvas.scale(shiningScale, shiningScale, shiningCenterX, shiningCenterY);
            canvas.drawBitmap(shiningBitmap, shiningStartX, shiningStartY, bitmapPaint);
            canvas.restore();
        }

        //draw icon
        canvas.save();
        canvas.scale(thumbScale, thumbScale, thumbCenterx, thumbCenterY);
        canvas.drawBitmap(bitmap, mLeftPadding, mTopPadding, bitmapPaint);
        canvas.restore();
    }

//    @Override
//    public void onClick(View v) {
//        if (!isChecked) {
//            zanAnimation();
//        } else {
//            unZanAnimation();
//        }
//    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle data = new Bundle();
        data.putParcelable("superData", super.onSaveInstanceState());
        data.putBoolean("isChecked", isChecked);
        return data;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle data = (Bundle) state;
        Parcelable superData = data.getParcelable("superData");
        super.onRestoreInstanceState(superData);
        isChecked = data.getBoolean("isChecked", false);
    }

    /**
     * 点赞动画
     */
    public void zanAnimation() {
        isAnimationFinished = false;
        ObjectAnimator unZanScaleAnimator = ObjectAnimator.ofFloat(this, "thumbScale", DEFAULT_THUMB_SCALE_NORMAL, DEFAULT_THUMB_SCALE_MIN);
        unZanScaleAnimator.setDuration(DEFAULT_SCALE_DURING);
        unZanScaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isChecked = true;
            }
        });

        ObjectAnimator zanScaleAnimator = ObjectAnimator.ofFloat(this, "thumbScale", DEFAULT_THUMB_SCALE_MIN, DEFAULT_THUMB_SCALE_MAX, DEFAULT_THUMB_SCALE_NORMAL);
        zanScaleAnimator.setDuration(DEFAULT_SCALE_DURING);

        ObjectAnimator zanCircleRadiusAnimator = ObjectAnimator.ofFloat(this, "circleRadius", minRadius, maxRadius);
        zanCircleRadiusAnimator.setDuration(DEFAULT_RADIUS_DURING);

        ObjectAnimator shiningScaleAnimator = ObjectAnimator.ofFloat(this, "shiningScale", DEFAULT_SHINING_SCALE_MIN, DEFAULT_SHINING_SCALE_NORMAL);
        shiningScaleAnimator.setDuration(DEFAULT_SCALE_DURING);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(zanScaleAnimator).after(unZanScaleAnimator);
        animatorSet.play(zanScaleAnimator)
                .with(zanCircleRadiusAnimator)
                .with(shiningScaleAnimator);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimationFinished = true;
            }
        });
    }

    /**
     * 取消点赞动画
     */
    public void unZanAnimation() {
        isAnimationFinished = false;
        isChecked = false;
        ObjectAnimator unZanScaleAnimator = ObjectAnimator.ofFloat(this, "thumbScale", DEFAULT_THUMB_SCALE_NORMAL, DEFAULT_THUMB_SCALE_MIN, DEFAULT_THUMB_SCALE_NORMAL);
        unZanScaleAnimator.setDuration(DEFAULT_SCALE_DURING + DEFAULT_RADIUS_DURING);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(unZanScaleAnimator);
        animatorSet.start();
        unZanScaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimationFinished = true;
            }
        });
    }

    /**
     * 设置thumb的缩放比例
     *
     * @param scale
     */
    private void setThumbScale(float scale) {
        this.thumbScale = scale;
        invalidate();
    }

    /**
     * 设置圆圈半径
     *
     * @param radius
     */
    private void setCircleRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    /**
     * 设置shining的缩放比例
     *
     * @param scale
     */
    private void setShiningScale(float scale) {
        this.shiningScale = scale;
        invalidate();
    }

    /**
     * 获取icon中心x坐标
     *
     * @return
     */
    public float getThumbCenterx() {
        return thumbCenterx;
    }

    /**
     * 获取icon中心y坐标
     *
     * @return
     */
    public float getThumbCenterY() {
        return thumbCenterY;
    }

    public void setUnZanResId(int unZanResId) {
        this.unZanResId = unZanResId;
        invalidate();
    }

    public void setZanResId(int zanResId) {
        this.zanResId = zanResId;
        invalidate();
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        invalidate();
    }

    public void setCircleStrokeWidth(float circleStrokeWidth) {
        this.circleStrokeWidth = circleStrokeWidth;
        invalidate();
    }

    public void setCircleRadiusSpacing(float circleRadiusSpacing) {
        this.circleRadiusSpacing = circleRadiusSpacing;
        invalidate();
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        invalidate();
    }
}
