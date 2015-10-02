package com.lovely3x.fireinstantmessager.views.activities;

import android.app.Activity;

import com.lovely3x.common.activities.CommonActivity;

import butterknife.ButterKnife;

/**
 * Created by lovely3x on 15-10-2.
 */
public class BaseActivity extends CommonActivity{


    /**
     * 绑定当前注解视图
     * 内部调用 {@link ButterKnife#bind(Activity)}
     */
    void bind(){
        ButterKnife.bind(this);
    }
}
