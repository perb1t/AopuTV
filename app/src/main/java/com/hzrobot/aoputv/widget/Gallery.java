package com.hzrobot.aoputv.widget;

import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.hzrobot.aoputv.utils.log.LogUtil;

/**
 * Created by shijiwei on 2018/1/8.
 *
 * @VERSION 1.0
 */

public class Gallery extends FrameLayout implements GestureDetector.OnGestureListener {

    private static final String TAG = "Gallery";

    private int mTouchSlop;

    private Scroller mScroller;
    private GestureDetector mGestureDetector;
    private BaseAdapter mGalleryAdapter;


    /**
     * 手机按下时的屏幕坐标
     */
    private float mXDown;

    /**
     * 手机当时所处的屏幕坐标
     */
    private float mXMove;

    /**
     * 上次触发ACTION_MOVE事件时的屏幕坐标
     */
    private float mXLastMove;

    /**
     * 界面可滚动的左边界
     */
    private int leftBorder;

    /**
     * 界面可滚动的右边界
     */
    private int rightBorder;

    public Gallery(@NonNull Context context) {
        this(context, null);
    }

    public Gallery(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Gallery(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ViewConfiguration cf = ViewConfiguration.get(context);
        mTouchSlop = cf.getScaledPagingTouchSlop();
        mScroller = new Scroller(getContext());

        mGestureDetector = new GestureDetector(getContext(), (GestureDetector.OnGestureListener) this);
        mGestureDetector.setIsLongpressEnabled(true);

        LogUtil.e(TAG, "mTouchSlop : " + mTouchSlop + "pixels");

    }

    public void setmGalleryAdapter(BaseAdapter galleryAdapter) {
        this.mGalleryAdapter = galleryAdapter;
    }

    //    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mXDown = ev.getRawX();
//                mXLastMove = mXDown;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mXMove = ev.getRawX();
//                float diff = Math.abs(mXMove - mXDown);
//                mXLastMove = mXMove;
//                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
//                if (diff > mTouchSlop) {
//                    return true;
//                }
//                break;
//        }
//        return super.onInterceptTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                mXMove = event.getRawX();
//                int scrolledX = (int) (mXLastMove - mXMove);
//                if (getScrollX() + scrolledX < leftBorder) {
//                    scrollTo(leftBorder, 0);
//                    return true;
//                } else if (getScrollX() + getWidth() + scrolledX > rightBorder) {
//                    scrollTo(rightBorder - getWidth(), 0);
//                    return true;
//                }
//                scrollBy(scrolledX, 0);
//                mXLastMove = mXMove;
//                break;
//            case MotionEvent.ACTION_UP:
//                // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
//                int targetIndex = (getScrollX() + getWidth() / 2) / getWidth();
//                int dx = targetIndex * getWidth() - getScrollX();
//                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
//                mScroller.startScroll(getScrollX(), 0, dx, 0);
//                invalidate();
//                break;
//        }
//        return super.onTouchEvent(event);
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LogUtil.e(TAG, " onLayout changed : " + changed);
        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                final int finalI = i;
                childView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtil.e(TAG, " position = " + finalI);
                    }
                });
                childView.layout(i * childView.getMeasuredWidth(), getMeasuredHeight() / 2 - childView.getMeasuredHeight() / 2, (i + 1) * childView.getMeasuredWidth(), getMeasuredHeight() / 2 + childView.getMeasuredHeight() / 2);
            }
            // 初始化左右边界值
            leftBorder = getChildAt(0).getLeft();
            rightBorder = getChildAt(getChildCount() - 1).getRight();
        }
    }


    PointF mStartPoint = new PointF();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        LogUtil.e(TAG, "getScrollX :" + getScrollX() + " getX():" + getX() + "  mScroller.getCurrX():" + mScroller.getCurrX());
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                return true;
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_UP:
//        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean mScrollState = false;
    float offset;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mStartPoint.set(event.getX(), event.getY());
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                offset = mStartPoint.x - event.getX();
//                LogUtil.e(TAG, "mScrollState = " + offset + " , " + event.getX() + " , " + getScrollX());
//                if (!mScrollState) {
//                    if (Math.abs(offset) > mTouchSlop) mScrollState = true;
//                } else {
//                    mStartPoint.set(event.getX(), event.getY());
//                    mScroller.startScroll(getScrollX(), 0, (int) -offset, 0);
//                    postInvalidate();
//
//                    return true;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                mScrollState = false;
//                break;
//        }
//
//        return super.onTouchEvent(event);


        // Give everything to the gesture detector
        boolean retValue = mGestureDetector.onTouchEvent(event);

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            // Helper method for lifted finger
            onUp();
        } else if (action == MotionEvent.ACTION_CANCEL) {
            onCancel();
        }

        return retValue;


    }

    private void onCancel() {
    }

    private void onUp() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            View c = getChildAt(0);
//            FrameLayout.LayoutParams layoutParams = (LayoutParams) c.getLayoutParams();
//            layoutParams.setMargins(layoutParams.getMarginStart() + (int)offset,0,0,0);
            LogUtil.e(TAG, "first child left :" + c.getLeft() + "," + c.getTop() + ", " + c.getRight() + " , " + c.getBottom());
            scrollBy((int) offset, 0);
            invalidate();
        }
    }

    /* GestureDetector start */

    @Override
    public boolean onDown(MotionEvent e) {
        LogUtil.e(TAG," onDown ");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        LogUtil.e(TAG," onShowPress ");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        LogUtil.e(TAG," onSingleTapUp ");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        LogUtil.e(TAG," onScroll ");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        LogUtil.e(TAG," onLongPress ");

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        LogUtil.e(TAG,"  onFling");
        return false;
    }

     /* GestureDetector end */


}
