package com.danielhan.hencoderimitaion.jikezan;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.danielhan.hencoderimitaion.R;
import com.danielhan.hencoderimitaion.utils.DensityUtil;


/**
 * 即刻点赞
 *
 * @author DanielHan
 * @date 2017/11/15
 */

public class JiKeZanView extends LinearLayout implements View.OnClickListener {

    public static final float DEFAULT_DRAWABLE_PADDING = 4f;

    private ZanIconView zanIconView;
    private ZanCountView zanCountView;
    /**
     * 自定义属性
     */
    private int unZanResId;
    private int zanResId;
    private int circleColor;
    private float circleStrokeWidth;
    //圆环半径差
    private float circleRadiusSpacing;
    private float mDrawablePadding;
    private float textSize;
    private int textColor;
    private int count;

    private int mTopMargin;
    private boolean mNeedChangeChildView;
    private boolean isChecked;

    public JiKeZanView(Context context) {
        this(context, null);
    }


    public JiKeZanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public JiKeZanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JiKeZanView);
        unZanResId = typedArray.getResourceId(R.styleable.JiKeZanView_zan_icon_unselected, ZanIconView.DEFAULT_UNZAN_RES_ID);
        zanResId = typedArray.getResourceId(R.styleable.JiKeZanView_zan_icon_selected, ZanIconView.DEFAULT_ZAN_RES_ID);
        circleColor = typedArray.getColor(R.styleable.JiKeZanView_zan_circle_color, ZanIconView.DEFAULT_CIRCLE_COLOR);
        circleStrokeWidth = DensityUtil.dip2px(context, ZanIconView.DEFAULT_CIRCLE_STROKE_WIDTH);
        circleStrokeWidth = typedArray.getDimension(R.styleable.JiKeZanView_zan_circle_stroke_width, circleStrokeWidth);
        circleRadiusSpacing = DensityUtil.dip2px(context, ZanIconView.DEFAULT_CIRCLE_RADIUS_SPACING);
        circleRadiusSpacing = typedArray.getDimension(R.styleable.JiKeZanView_zan_circle_radius_spacing, circleRadiusSpacing);
        mDrawablePadding = DensityUtil.dip2px(context, DEFAULT_DRAWABLE_PADDING);
        mDrawablePadding = typedArray.getDimension(R.styleable.JiKeZanView_zan_drawable_padding, mDrawablePadding);
        textSize = DensityUtil.sp2px(context, ZanCountView.DEFAULT_TEXT_SIZE);
        textSize = typedArray.getDimension(R.styleable.JiKeZanView_zan_text_size, textSize);
        textColor = typedArray.getColor(R.styleable.JiKeZanView_zan_text_color, ZanCountView.DEFAULT_TEXT_COLOR);
        count = typedArray.getInt(R.styleable.JiKeZanView_zan_count, ZanCountView.DEFAULT_COUNT);
        typedArray.recycle();
        init();
    }

    private void init() {
        removeAllViews();
        setClipChildren(false);
        setOrientation(LinearLayout.HORIZONTAL);

        addIconView();

        addCountView();

        //把设置的padding分解到子view，否则对超出view范围的动画显示不全
        setPadding(0, 0, 0, 0, false);
        setOnClickListener(this);
    }

    @SuppressWarnings("SameParameterValue")
    public void setPadding(int left, int top, int right, int bottom, boolean needChange) {
        this.mNeedChangeChildView = needChange;
        setPadding(left, top, right, bottom);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (mNeedChangeChildView) {
            resetIconParams();
            resetCountParams();
            mNeedChangeChildView = false;
        } else {
            super.setPadding(left, top, right, bottom);
        }
    }

    private void resetIconParams() {
        LayoutParams params = (LayoutParams) zanIconView.getLayoutParams();
        if (mTopMargin < 0) {
            params.topMargin = mTopMargin;//设置这个距离是为了文字与拇指居中显示
        }
        params.leftMargin = getPaddingLeft();
        params.topMargin += getPaddingTop();
        params.bottomMargin = getPaddingBottom();
        zanIconView.setLayoutParams(params);
    }

    private void resetCountParams() {
        LayoutParams params = (LayoutParams) zanCountView.getLayoutParams();
        if (mTopMargin > 0) {
            params.topMargin = mTopMargin;//设置这个距离是为了文字与拇指居中显示
        }
        params.leftMargin = (int) mDrawablePadding;
        params.topMargin += getPaddingTop();
        params.bottomMargin = getPaddingBottom();
        params.rightMargin = getPaddingRight();
        zanCountView.setLayoutParams(params);
    }

    private void addIconView() {
        zanIconView = new ZanIconView(getContext());
        zanIconView.setUnZanResId(unZanResId);
        zanIconView.setZanResId(zanResId);
        zanIconView.setCircleColor(circleColor);
        zanIconView.setCircleStrokeWidth(circleStrokeWidth);
        zanIconView.setCircleRadiusSpacing(circleRadiusSpacing);
        mTopMargin = (int) (zanIconView.getThumbCenterY() - textSize / 2);
        addView(zanIconView, getIconParams());
    }

    private void addCountView() {
        zanCountView = new ZanCountView(getContext());
        zanCountView.setTextColor(textColor);
        zanCountView.setTextSize(textSize);
        zanCountView.setCount(count);
        addView(zanCountView, getCountParams());
    }

    private LayoutParams getIconParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mTopMargin < 0) {
            params.topMargin = mTopMargin;//设置这个距离是为了文字与拇指居中显示
        }
        params.leftMargin = getPaddingLeft();
        params.topMargin += getPaddingTop();
        params.bottomMargin = getPaddingBottom();
        return params;
    }

    private LayoutParams getCountParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mTopMargin > 0) {
            params.topMargin = mTopMargin;//设置这个距离是为了文字与拇指居中显示
        }
        params.leftMargin = (int) mDrawablePadding;
        params.topMargin += getPaddingTop();
        params.bottomMargin = getPaddingBottom();
        params.rightMargin = getPaddingRight();
        return params;
    }

    @Override
    public void onClick(View v) {
        isChecked = !isChecked;
        if (isChecked) {
            count++;
            zanCountView.calculateChangeNum(1);
            zanIconView.zanAnimation();
        } else {
            count--;
            zanCountView.calculateChangeNum(-1);
            zanIconView.unZanAnimation();
        }

    }

    public void setCount(int count) {
        this.count = count;
        zanCountView.setCount(count);
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        zanIconView.setChecked(isChecked);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle data = new Bundle();
        data.putParcelable("superData", super.onSaveInstanceState());
        data.putBoolean("isChecked", isChecked);
        data.putInt("count", count);
        return data;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle data = (Bundle) state;
        Parcelable superData = data.getParcelable("superData");
        super.onRestoreInstanceState(superData);
        isChecked = data.getBoolean("isChecked", false);
        count = data.getInt("count", ZanCountView.DEFAULT_COUNT);
        setCount(count);
        setChecked(isChecked);
    }

}
