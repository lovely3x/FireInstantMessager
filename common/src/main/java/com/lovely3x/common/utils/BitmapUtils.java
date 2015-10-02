package com.lovely3x.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * Created by lovely3x on 15-9-11.
 * bitmap utils
 */
public class BitmapUtils {


    /**
     * 获取缩略图
     *
     * @param bm   原图对象
     * @param size 图片的尺寸
     * @return
     */
    public static final Bitmap getThumbnail(Bitmap bm, int size) {
        return Bitmap.createScaledBitmap(bm, size, size, false);
    }

    /**
     * 获取缩略图
     *
     * @param bm     原图对象
     * @param width  缩略图的宽度
     * @param height 缩略图的高度
     * @return
     */
    public static final Bitmap getThumbnail(Bitmap bm, int width, int height) {
        return Bitmap.createScaledBitmap(bm, width, height, false);
    }

    /**
     * 获取缩略图
     *
     * @param filePath 缩略图的文件地址
     * @param width    缩略图的宽度
     * @param height   缩略图的高度
     * @return
     */
    public static Bitmap getThumbnail(String filePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bm = BitmapFactory.decodeFile(filePath, options);

        //int realWidth = options.outWidth >= width ? width : options.outWidth;
        //int realHeight = options.outHeight >= height ? height : options.outHeight;

        float scaleX = options.outWidth >= width ? 1.0f * options.outWidth / width : 1;
        float scaleY = options.outHeight >= height ? 1.0f * options.outHeight / height : 1;
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) Math.min(scaleX, scaleY);
        return BitmapFactory.decodeFile(filePath, options);
    }
}
