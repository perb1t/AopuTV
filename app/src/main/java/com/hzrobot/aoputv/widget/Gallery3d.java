package com.hzrobot.aoputv.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.hzrobot.aoputv.utils.log.LogUtil;

/**
 * Created by shijiwei on 2018/1/10.
 *
 * @VERSION 1.0
 */

public class Gallery3d extends android.widget.Gallery {

    //最大的旋转角度
    private int MAX_ROTATE_ANGLE = 50;
    //最大缩放值
    private int MAX_ZOOM = -250;
    //记录中间点的位置
    private int currentOfGallery;
    //创建相机对象
    private Camera camera = new Camera();

    public Gallery3d(Context context) {
        super(context);
        setStaticTransformationsEnabled(true);
    }

    public Gallery3d(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStaticTransformationsEnabled(true);
    }

    public Gallery3d(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setStaticTransformationsEnabled(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        currentOfGallery = getCurrentOfGallery();
        LogUtil.e("=====", "onSizeChanged  currentOfGallery: " + currentOfGallery);
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {

        //得到图片的中心点
        int currentOfChild = getCurrentOfView(child);
        int width = child.getLayoutParams().width;
        int height = child.getLayoutParams().height;
        //旋转的角度
        int rotateAngle = 0;
        t.clear();
        //设置图片变形样式
        t.setTransformationType(Transformation.TYPE_MATRIX);
        //位置中心点位置
        if (currentOfChild == currentOfGallery) {
            transformationBitmap((ImageView) child, t, 0);
        } else {    //不是中心位置
            rotateAngle = (int) ((float) (currentOfGallery - currentOfChild) / width * MAX_ROTATE_ANGLE);
            if (Math.abs(rotateAngle) > MAX_ROTATE_ANGLE) {
                rotateAngle = rotateAngle < 0 ? -MAX_ROTATE_ANGLE : MAX_ROTATE_ANGLE;
            }
            //图片变形
            transformationBitmap((ImageView) child, t, rotateAngle);
        }

//        View childAt = getChildAt(targetPosition);
//        transformationBitmap((ImageView) childAt, t, 0);
        return true;
    }

    /**
     * 图片变形
     *
     * @param child
     * @param t
     * @param
     */
    private void transformationBitmap(ImageView child, Transformation t, int rotateAngle) {
        //保存图像变化的效果
        camera.save();
        Matrix imageMatrix = t.getMatrix();
        int rotate = Math.abs(rotateAngle);
        int imageWidth = child.getWidth();
        int imageHeight = child.getHeight();
        //z:正数:图片变大
        //x:水平移动
        //y:垂直移动
        camera.translate(0.0f, 0.0f, 100.0f);
        //当前旋转角度小于最大旋转角度
        if (rotate < MAX_ROTATE_ANGLE) {
            float zoom = (float) ((rotate * 1.5) + MAX_ZOOM);
            camera.translate(0.0f, 0.0f, zoom);
            //设置图片渐变效果
            child.setAlpha((int) (255 - rotate * 2.5));
        }
        //图片向展示中心进行垂直角度旋转
        camera.rotateY(rotateAngle);
        camera.getMatrix(imageMatrix);

        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        imageMatrix.postTranslate(imageWidth / 2, imageHeight / 2);
        //还原图像变化的效果
        camera.restore();
    }

    /**
     * 获取Gallery展示图片的中心点
     *
     * @return
     */
    public int getCurrentOfGallery() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    /**
     * 获取图片中心点
     *
     * @param view
     * @return
     */
    public int getCurrentOfView(View view) {
        return view.getLeft() + view.getWidth() / 2;
    }


    public void moveToNext() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mScrollChildHandler.removeCallbacks(mScrollChildRunnable);
            performAccessibilityAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, null);
        }
    }

    public void moveToPrevious() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mScrollChildHandler.removeCallbacks(mScrollChildRunnable);
            performAccessibilityAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, null);
        }
    }

    /**
     * 1 <= position <= getAdaper.getCount()
     *
     * @param position
     */
    private int targetPosition = 0;

    public void moveToSpecificPosition(int position) {
        int currentPosition = getSelectedItemPosition();
        targetPosition = position;
        mStep = currentPosition - (position - 1);

        mScrollChildHandler.removeCallbacks(mScrollChildRunnable);
        mScrollChildHandler.postDelayed(mScrollChildRunnable, 0);

    }


    private int mStep = 0;
    private Handler mScrollChildHandler = new Handler();
    private Runnable mScrollChildRunnable = new Runnable() {
        @Override
        public void run() {

            if (mStep != 0) {
                int duration = 400;
                if (Math.abs(mStep) > 1) {
//                    //more than 10 steps
                    duration = 20;
                } else {
                    //  less than 10 steps
                    duration = 400;
                }
                setAnimationDuration(duration);
                if (mStep < 0) {
                    mStep++;
                    moveToNext();
                } else {
                    mStep--;
                    moveToPrevious();
                }
                mScrollChildHandler.postDelayed(this, duration + 10);

            } else {
                if (getSelectedItemPosition() + 1 != targetPosition) {
                    moveToSpecificPosition(targetPosition);
                } else {

                }
            }

        }
    };

}
