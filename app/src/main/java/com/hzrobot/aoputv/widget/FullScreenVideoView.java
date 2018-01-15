package com.hzrobot.aoputv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by shijiwei on 2017/12/5.
 *
 * @VERSION 1.0
 */

public class FullScreenVideoView extends VideoView {

    public FullScreenVideoView(Context context) {
        this(context, null);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(0, widthMeasureSpec);//得到默认的大小（0，宽度测量规范）
        int height = getDefaultSize(0, heightMeasureSpec);//得到默认的大小（0，高度度测量规范）
        setMeasuredDimension(width, height);
    }
}
