package com.lovely3x.common.activities;

import android.view.View;
import android.view.ViewGroup;

import com.lovely3x.common.R;

/**
 * 默认实现的空内容提示activity
 * Created by lovely3x on 15-8-16.
 */
public abstract class DefaultEmptyContentTipActivity extends EmptyContentTipActivity {
    /**
     * 用户设置的内容
     */
    private View mContentView;

    /**
     * 过渡视图
     */
    private View transitionView;

    @Override
    public void setContentView(int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    /**
     * 获取视图
     *
     * @return 子类返回想要显示的视图布局id
     */
    protected abstract int getContentView();


    @Override
    public void setContentView(View view) {
        setContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentView = view;
        super.setContentView(view, params);
    }

    @Override
    public void handleEmptyContent() {
        contentCheck();
    }

    @Override
    public void handleEmptyContent(View.OnClickListener listener) {

    }

    @Override
    public void handleLoadingContent() {
        contentCheck();
        ViewGroup decor = getContentParent();
        transitionView = getLayoutInflater().inflate(R.layout.view_loading, decor, false);
        decor.addView(transitionView);
    }

    private ViewGroup getContentParent() {
        return (ViewGroup) mContentView.getParent();

    }

    @Override
    public void handleLoadSuccessful() {
        contentCheck();
        if (transitionView != null) {
            getContentParent().removeView(transitionView);
        }
    }

    @Override
    public void handleLoadFailure(int errorCode) {
        contentCheck();
    }

    @Override
    public void handleLoadFailure(String errorMsg) {

    }

    @Override
    public void handleLoadFailure(String errorMsg, View.OnClickListener retryListener) {

    }

    @Override
    public void handleLoadFailure(int errorCode, View.OnClickListener retryListener) {

    }

    /**
     * 获取网络错误代码
     * 默认-1 表示网络问题
     * @return
     */
    protected int getNetWorkErrorCode() {
        return -1;
    }

    /**
     * 是否设置了ContentView检查
     */
    private void contentCheck() {
        if (mContentView == null)
            throw new IllegalStateException("call this method before please call setContentView method");
    }
}
