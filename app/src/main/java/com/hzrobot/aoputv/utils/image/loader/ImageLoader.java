package com.hzrobot.aoputv.utils.image.loader;

import android.content.Context;
import android.graphics.Bitmap;

import com.hzrobot.aoputv.utils.image.cache.Cache;
import com.hzrobot.aoputv.utils.image.cache.MemoryCache;

/**
 * Created by shijiwei on 2018/1/12.
 *
 * @VERSION 1.0
 */

public class ImageLoader {

    /* image cache */
    private Cache cache;

    private Context ctx;

    public ImageLoader(Context ctx) {
        this.ctx = ctx;
        cache = new MemoryCache();
    }


    public Bitmap get(String key) {
        return cache == null ? null : cache.get(key);
    }

    public void put(String key, Bitmap bitmap) {
        if (cache != null)
            cache.put(key, bitmap);
    }

    public Bitmap get(int resId) {
        return get(formatResource(ctx, resId));
    }

    public void put(int resId, Bitmap bitmap) {
        put(formatResource(ctx, resId), bitmap);
    }

    /**
     * 格式 RES 资源文件ID 为字符串类型
     *
     * @param ctx
     * @param resId
     * @return
     */
    private String formatResource(Context ctx, int resId) {
        String resType = ctx.getResources().getResourceTypeName(resId);
        String resName = ctx.getResources().getResourceEntryName(resId);
        return "R." + resType + "." + resName;
    }

    /**
     * 注入用户自定义实现的Cache、增强扩展性
     *
     * @param cache
     */
    public void setCache(Cache cache) {
        this.cache = cache;
    }
}
