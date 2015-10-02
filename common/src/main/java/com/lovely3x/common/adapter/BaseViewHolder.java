package com.lovely3x.common.adapter;

import android.view.View;

import butterknife.ButterKnife;

/**
 * base view holder hold the view
 * Created by lovely3x on 15-7-9.
 */
public class BaseViewHolder {

    public final View mRootView;


    /**
     * please don't modify the constructor
     *
     * @param rootView the root view
     */
    public BaseViewHolder(View rootView) {
        this.mRootView = rootView;
        ButterKnife.bind(this, rootView);
    }
}
