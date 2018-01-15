package com.hzrobot.aoputv.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by shijiwei on 2018/1/8.
 *
 * @VERSION 1.0
 */

public class BitmapHelper {

    /**
     * 创建带有倒影的图片
     */
    public static Drawable createRefectedBitmap(Context ctx, int resId) {
        //原图片与倒影图片之间的距离
        int refectionGap = 4;
        //向图片数组中加入图片
        //原图片
        Bitmap resourceBitmap = BitmapFactory.decodeResource(ctx.getResources(), resId);
        int width = resourceBitmap.getWidth();
        int height = resourceBitmap.getHeight();
        //倒影图片
        //reource:原图片
        //x,y:生成倒影图片的起始位置
        //width,heiht:生成倒影图片宽和高
        //Matrix m:用来设置图片的样式(倒影)
        Matrix m = new Matrix();
        //x:水平翻转；y:垂直翻转   1支持； -1翻转
        m.setScale(1, -1);
        Bitmap refrectionBitmap = Bitmap.createBitmap(resourceBitmap, 0, height / 2, width, height / 2, m, false);
        //合成的带有倒影的图片
        Bitmap bitmap = Bitmap.createBitmap(width, height + height / 2, Bitmap.Config.ARGB_8888);
        //创建画布
        Canvas canvas = new Canvas(bitmap);
        //绘制原图片
        canvas.drawBitmap(resourceBitmap, 0, 0, null);
        //绘制原图片与倒影之间的间隔
        Paint defaultPaint = new Paint();
        defaultPaint.setAntiAlias(true);
        canvas.drawRect(0, height, width, height + refectionGap, defaultPaint);
        //绘制倒影图片
        canvas.drawBitmap(refrectionBitmap, 0, height + refectionGap, null);

        //ps中的渐变和遮罩效果
        Paint paint = new Paint();
        //设置遮罩效果
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //设置渐变效果
        //设置着色器为遮罩着色
        LinearGradient shader = new LinearGradient(0, height, 0, bitmap.getHeight(), 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        canvas.drawRect(0, height, width, bitmap.getHeight(), paint);

        //创建BitmapDrawable图片
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        //消除图片锯齿效果，使图片平滑
        bd.setAntiAlias(true);
        return bd;
    }
}
