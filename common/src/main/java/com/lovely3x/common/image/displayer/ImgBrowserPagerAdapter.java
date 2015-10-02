package com.lovely3x.common.image.displayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lovely3x.common.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ViewAware;

import java.io.File;
import java.util.List;

/**
 * 图片浏览器的适配器
 * Created by lovely3x on 15-9-22.
 */
public class ImgBrowserPagerAdapter extends PagerAdapter {

    /**
     * 图片资源类型 bitmap
     */
    public static final int IMG_SOURCE_TYPE_BITMAP = 0x1;
    /**
     * 图片资源类型 网络地址
     */
    public static final int IMG_SOURCE_TYPE_URL = 0x2;
    /**
     * 图片资源类型 文件地址
     */
    public static final int IMG_SOURCE_TYPE_FILE = 0x3;

    /**
     * 图片资源类型 assert文件资源
     */
    public static final int IMG_SOURCE_TYPE_ASSERT = 0x4;
    private final List<? extends Object> mImgs;
    private final boolean mHasAnim;
    private final int mContentType;
    private final Context mContext;
    private int LoadFailureRes;
    private int mLoadingImgRes;
    private Bitmap mLoadFailureImgBitmap;
    private Bitmap mLoadingImgBitmap;

    private OnItemClickedListener mOnItemClickedListener;


    public ImgBrowserPagerAdapter(Context context, List<? extends Object> imgs,
                                  int contentType, boolean hasAnim,
                                  @DrawableRes int loadingRes,
                                  @DrawableRes int loadFailureRes) {
        this.mImgs = imgs;
        this.mContentType = contentType;
        this.mHasAnim = hasAnim;
        this.mLoadingImgRes = loadingRes;
        this.LoadFailureRes = loadFailureRes;
        this.mContext = context;

    }

    public ImgBrowserPagerAdapter(Context context, List<? extends Object> imgs,
                                  int contentType, boolean hasAnim,
                                  Bitmap loading,
                                  Bitmap loadFailure) {
        this.mImgs = imgs;
        this.mContentType = contentType;
        this.mHasAnim = hasAnim;
        this.mLoadingImgBitmap = loading;
        this.mLoadFailureImgBitmap = loading;
        this.mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        SubsamplingScaleImageView ssiv = new SubsamplingScaleImageView(mContext);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(ssiv,lp);

        switch (mContentType) {
            case IMG_SOURCE_TYPE_ASSERT:
                ssiv.setImage(ImageSource.asset((String) mImgs.get(position)));
                break;
            case IMG_SOURCE_TYPE_URL:
                ImageLoader.getInstance().displayImage((String) mImgs.get(position), new ViewAware(ssiv) {
                    @Override
                    protected void setImageDrawableInto(Drawable drawable, View view) {
                    }

                    @Override
                    protected void setImageBitmapInto(Bitmap bitmap, View view) {
                        ((SubsamplingScaleImageView) view).setImage(ImageSource.bitmap(bitmap));
                    }
                }, getOptions());
                break;
            case IMG_SOURCE_TYPE_FILE:
                ssiv.setImage(ImageSource.uri((String) mImgs.get(position)));
                break;
            case IMG_SOURCE_TYPE_BITMAP:
                ssiv.setImage(ImageSource.bitmap((Bitmap) mImgs.get(position)));
                break;
        }
        ssiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) mOnItemClickedListener.onItemClicked(position);
            }
        });
        return ssiv;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);

    }

    private DisplayImageOptions getOptions() {
        if (mLoadingImgBitmap != null || mLoadFailureImgBitmap != null) {
            return new DisplayImageOptions.Builder().showImageOnLoading(new BitmapDrawable(mContext.getResources(), mLoadingImgBitmap)).showImageOnFail(new BitmapDrawable(mContext.getResources(), mLoadFailureImgBitmap)).build();
        } else {
            return new DisplayImageOptions.Builder().showImageOnLoading(mLoadingImgRes).showImageOnFail(LoadFailureRes).build();
        }
    }


    @Override
    public int getCount() {
        return mImgs == null ? 0 : mImgs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setOnItemClicked(OnItemClickedListener listener) {
        this.mOnItemClickedListener = listener;
    }

    public static interface OnItemClickedListener {

        void onItemClicked(int position);
    }
}
