package com.lovely3x.common.activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

import com.lovely3x.common.activities.DefaultEmptyContentTipActivity;

import butterknife.ButterKnife;

/**
 * butterKnife activity
 * 试图使用butterKnife来简化操作的activity
 * Created by lovely3x on 15-8-16.
 */
public abstract class ButterKnifeActivity extends DefaultEmptyContentTipActivity {
    @Override
    protected void initViews() {
        ButterKnife.bind(this);
    }
}
