package com.danielhan.hencoderimitaion.flipboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.danielhan.hencoderimitaion.R;

/**
 * @author DanielHan
 * @date 2017/11/16
 */

public class FlipboardView extends View {

    private static final float DEFAULT_DEGREE_Y_MAX = -45;

    private Paint paint;
    private Bitmap bitmap;
    private int mLeftPadding, mTopPadding, mRightPadding, mBottomPadding;
    //Y轴方向旋转角度
    private float degreeY;
    //不变的那一半，Y轴方向旋转角度
    private float fixDegreeY;
    //Z轴方向（平面内）旋转的角度
    private float degreeZ;
    private int bitmapWidth, bitmapHeight;
    private int centerX, centerY;

    private Camera camera = new Camera();


    public FlipboardView(Context context) {
        this(context, null);
    }

    public FlipboardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flipboard);
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        mLeftPadding = getPaddingLeft();
        mTopPadding = getPaddingTop();
        mRightPadding = getPaddingRight();
        mBottomPadding = getPaddingBottom();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float newZ = -displayMetrics.density * 5;
        camera.setLocation(0, 0, newZ);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (int) (mLeftPadding + bitmapWidth + mRightPadding);
        int height = (int) (mTopPadding + bitmapHeight + mBottomPadding);
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
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        int x = centerX - bitmapWidth / 2;
        int y = centerY - bitmapHeight / 2;

        //画变换的一半
        //先旋转，再裁切，再使用camera执行3D动效,**然后保存camera效果**,最后再旋转回来
        canvas.save();
        camera.save();
        canvas.translate(centerX, centerY);
        canvas.rotate(-degreeZ);
        camera.rotateY(degreeY);
        camera.applyToCanvas(canvas);
        //计算裁切参数时清注意，此时的canvas的坐标系已经移动
        canvas.clipRect(0, -centerY, centerX, centerY);
        canvas.rotate(degreeZ);
        canvas.translate(-centerX, -centerY);
        camera.restore();
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();

        //画不变换的另一半
        canvas.save();
        camera.save();
        canvas.translate(centerX, centerY);
        canvas.rotate(-degreeZ);
        //计算裁切参数时清注意，此时的canvas的坐标系已经移动
        canvas.clipRect(-centerX, -centerY, 0, centerY);
        //此时的canvas的坐标系已经旋转，所以这里是rotateY
        camera.rotateY(fixDegreeY);
        camera.applyToCanvas(canvas);
        canvas.rotate(degreeZ);
        canvas.translate(-centerX, -centerY);
        camera.restore();
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();
    }

    private void startAnimation() {
        ObjectAnimator rotateY = ObjectAnimator.ofFloat(this, "degreeY", 0, DEFAULT_DEGREE_Y_MAX);
        rotateY.setDuration(2000);
        rotateY.setStartDelay(500);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "degreeZ", 0, 270);
        animator2.setDuration(5000);
        animator2.setStartDelay(500);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(this, "fixDegreeY", 0, 30);
        animator3.setDuration(2000);
        animator3.setStartDelay(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(rotateY, animator2,animator3);
        animatorSet.start();
    }

    public float getDegreeY() {
        return degreeY;
    }

    public void setDegreeY(float degreeY) {
        this.degreeY = degreeY;
        invalidate();
    }

    public float getDegreeZ() {
        return degreeZ;
    }

    public void setDegreeZ(float degreeZ) {
        this.degreeZ = degreeZ;
        invalidate();
    }

    @Keep
    public void setFixDegreeY(float fixDegreeY) {
        this.fixDegreeY = fixDegreeY;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }
}
