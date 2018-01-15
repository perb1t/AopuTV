package com.hzrobot.aoputv.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hzrobot.aoputv.R;
import com.hzrobot.aoputv.utils.image.cache.MemoryCache;
import com.hzrobot.aoputv.utils.log.LogUtil;
import com.hzrobot.aoputv.widget.Gallery3d;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shijiwei on 2018/1/10.
 *
 * @VERSION 1.0
 */

public class Gallery3dAdapter extends BaseAdapter {

    private static final String TAG = "Gallery3dAdapter";

    private List<String> imagePathSet;
    private Context ctx;

    private MemoryCache memoryCache;

    private Point displaySize = new Point();

    public Gallery3dAdapter(List<String> imagePathSet, Context ctx) {
        this.imagePathSet = imagePathSet;
        this.ctx = ctx;
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(displaySize);

        memoryCache = new MemoryCache();
    }

    @Override
    public int getCount() {
        return imagePathSet == null ? 0 : imagePathSet.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePathSet == null ? null : imagePathSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.layout_gallery, null);
            holder.photo = convertView.findViewById(R.id.iv_photo);
            holder.photo.setLayoutParams(new Gallery3d.LayoutParams(displaySize.x / 9 * 2, displaySize.x / 4));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mPictureHandler.execute(new PictureRunnable(imagePathSet.get(position), holder.photo));
        return convertView;
    }

    class ViewHolder {
        ImageView photo;
    }

    private ExecutorService mPictureHandler = Executors.newCachedThreadPool();
    private Handler mPictureObserver = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            XX x = (XX) msg.obj;
            x.iv.setImageDrawable(x.drawable);
            return false;
        }
    });

    class PictureRunnable implements Runnable {

        private String path;
        private ImageView iv;

        public PictureRunnable(String path, ImageView iv) {
            this.path = path;
            this.iv = iv;
        }

        @Override
        public void run() {
            Message message = mPictureObserver.obtainMessage();
            Bitmap image = memoryCache.get(path);
            if (image == null) {
                image = loadPicture(path);
            } else {
                LogUtil.e("======", "=========AAAAAAAA========");
            }
            memoryCache.put(path, image);
            message.obj = new XX(inverted(image), iv);
            mPictureObserver.sendMessage(message);
        }
    }

    private static class XX {
        Drawable drawable;
        ImageView iv;

        public XX(Drawable drawable, ImageView iv) {
            this.drawable = drawable;
            this.iv = iv;
        }
    }

    private Bitmap loadPicture(String path) {

        BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, ops);
        int scale = ops.outWidth / ((displaySize.x / 9) * 2);
        ops.inSampleSize = scale + 1;
        ops.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, ops);
    }

    /**
     * 创建带有倒影的图片
     */
    public Drawable inverted(Bitmap originBitmap) {
        //原图片与倒影图片之间的距离
        int invertedSpacing = 4;
        //原图片
        int width = originBitmap.getWidth();
        int height = originBitmap.getHeight();
        //倒影图片
        //reource:原图片
        //x,y:生成倒影图片的起始位置
        //width,heiht:生成倒影图片宽和高
        //Matrix m:用来设置图片的样式(倒影)
        Matrix m = new Matrix();
        //x:水平翻转；y:垂直翻转   1支持； -1翻转
        m.setScale(1, -1);
        Bitmap refrectionBitmap = Bitmap.createBitmap(originBitmap, 0, height / 2, width, height / 2, m, false);
        //合成的带有倒影的图片
        Bitmap bitmap = Bitmap.createBitmap(width, height + height / 2, Bitmap.Config.ARGB_8888);
        //创建画布
        Canvas canvas = new Canvas(bitmap);
        //绘制原图片
        canvas.drawBitmap(originBitmap, 0, 0, null);
        //绘制原图片与倒影之间的间隔
        Paint defaultPaint = new Paint();
        defaultPaint.setAntiAlias(true);
        canvas.drawRect(0, height, width, height + invertedSpacing, defaultPaint);
        //绘制倒影图片
        canvas.drawBitmap(refrectionBitmap, 0, height + invertedSpacing, null);

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
