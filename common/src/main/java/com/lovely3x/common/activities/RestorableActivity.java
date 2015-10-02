package com.lovely3x.common.activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

/**
 * 可恢复实例状态的activity
 * Created by lovely3x on 15-8-16.
 */
public abstract class RestorableActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) restoreInstanceOnCreateBefore(savedInstanceState);
        setContentView(getContentView());
        initViews();
        onViewInitialized();
        if (savedInstanceState != null) restoreInstanceOnCreateAfter(savedInstanceState);
    }

    /**
     * 获取子类设置的布局id
     *
     * @return 子类设置的布局id
     */
    @LayoutRes
    protected abstract int getContentView();


    /**
     * 视图初始化完成
     * 这个方法执行完成之后将立即执行{@link #restoreInstanceOnCreateAfter(Bundle)}
     */
    protected abstract void onViewInitialized();

    /**
     * 初始化视图
     */
    protected abstract void initViews();

    /**
     * 恢复实例状态
     * 会在在onCreate之前调用
     *
     * @param savedInstance 保存实例状态的载体
     */
    public abstract void restoreInstanceOnCreateBefore(@NonNull Bundle savedInstance);

    /**
     * 恢复实例状态
     * 会在在onCreate执行完毕后调用
     *
     * @param savedInstance 保存实例状态的载体
     */
    public abstract void restoreInstanceOnCreateAfter(@NonNull Bundle savedInstance);

    /**
     * 保存实例状态
     *
     * @param outState 保存实例状态的载体
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
    }
}
