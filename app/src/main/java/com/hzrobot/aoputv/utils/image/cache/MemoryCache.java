package com.hzrobot.aoputv.utils.image.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.hzrobot.aoputv.utils.log.LogUtil;

import java.lang.ref.SoftReference;

/**
 * Created by shijiwei on 2018/1/12.
 *
 * @VERSION 1.0
 */

public class MemoryCache implements Cache {

    private static final String TAG = "MemoryCache";

    private LruCache<String, SoftReference<Bitmap>> lruCache;

    public MemoryCache() {

        lruCache = new LruCache<String, SoftReference<Bitmap>>(maxSize()) {
            @Override
            protected int sizeOf(String key, SoftReference<Bitmap> value) {
                int size = 1;
                Bitmap image = value.get();
                if (image != null) {
                    size = image.getByteCount();
                }

                LogUtil.e(TAG, "sizeOf() "
                        + "\n key : " + key
                        + "\n entry size : " + (float) size / 1024 / 1024 + " M"
                        + "\n maxSize : " + (float) maxSize() / 1024 / 1024 + " M"
                );

                return size;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, SoftReference<Bitmap> oldValue, SoftReference<Bitmap> newValue) {
//                super.entryRemoved(evicted, key, oldValue, newValue);
//                if (evicted && oldValue != null) {
//                    Bitmap image = oldValue.get();
//                    if (image != null) image.recycle();
//                }
            }
        };

    }

    @Override
    public Bitmap get(String key) {
        SoftReference<Bitmap> sr = lruCache.get(key);
        LogUtil.e(TAG, "get() : " + lruCache.size());
        return sr == null ? null : sr.get();
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        try {
//            SoftReference<Bitmap> sr = lruCache.get(key);
//            if (sr == null) {
//                lruCache.put(key, new SoftReference<Bitmap>(bitmap));
//            } else {
//                if (sr.get() == null) {
//                    lruCache.put(key, new SoftReference<Bitmap>(bitmap));
//                }
//            }
            lruCache.put(key,new SoftReference<Bitmap>(bitmap));
        } catch (Exception e) {

            LogUtil.e(TAG, "put()"
                    + " \n put entry failed  Cache Size : "
                    + (float) lruCache.size() / 1024 / 1024 + " M"
            );
        }

    }

    @Override
    public int size() {
        return lruCache.size();
    }

    @Override
    public int maxSize() {
        return (int) (Runtime.getRuntime().maxMemory() / 8);
    }

    @Override
    public void clear() {

    }
}
