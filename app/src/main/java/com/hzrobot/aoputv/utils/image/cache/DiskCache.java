package com.hzrobot.aoputv.utils.image.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by shijiwei on 2018/1/12.
 *
 * @VERSION 1.0
 */

public class DiskCache implements Cache {

    private final String prefixSDcard = "/sdcard";
    private final String prefixResource = "R.";

    private Context ctx;

    @Override
    public Bitmap get(String key) {
        Bitmap image = null;
        if (key.startsWith(prefixSDcard)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(key, opts);
            int originWidth = opts.outWidth;
            int originHeight = opts.outHeight;
        } else {

        }

        return null;
    }

    @Override
    public void put(String key, Bitmap bitmap) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int maxSize() {
        return 0;
    }

    @Override
    public void clear() {

    }
}
