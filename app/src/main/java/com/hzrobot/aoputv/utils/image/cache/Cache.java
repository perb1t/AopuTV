package com.hzrobot.aoputv.utils.image.cache;

import android.graphics.Bitmap;

/**
 * Created by shijiwei on 2018/1/12.
 *
 * @VERSION 1.0
 */

public interface Cache {

    /** Retrieve an image for the specified {@code key} or {@code null}. */
    Bitmap get(String key);

    /** Store an image in the cache for the specified {@code key}. */
    void put(String key, Bitmap bitmap);

    /** Returns the current size of the cache in bytes. */
    int size();

    /** Returns the maximum size in bytes that the cache can hold. */
    int maxSize();

    /** Clears the cache. */
    void clear();


}
