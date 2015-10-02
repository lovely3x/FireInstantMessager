package com.lovely3x.common.activities;

import android.view.View;

/**
 * Created by lovely3x on 15-8-16.
 * 空内容提示activity
 * <p/>
 * 当前没有内容时提示
 * 或者正在加载提示
 * 加载完成取消提示
 * 子类应该覆盖这个类的状态变化视图切换方法
 */
public abstract class EmptyContentTipActivity extends TitleActivity {

    /**
     * 当前的状态是 当前没有可以显示的内容
     */
    public static final int EMPTY_CONTENT = 1;
    /**
     * 当前的状态是 当前正在加载内容
     */
    public static final int LOADING_CONTENT = 2;
    /**
     * 当前的状态是 加载成功
     */
    public static final int LOADING_SUCCESSFUL = 3;

    /**
     * 当前的状态是 加载失败
     */
    public static final int LOADING_FAILURE = 4;

    /**
     * 当没有内容可供显示时调用这个方法
     */
    public abstract void handleEmptyContent();

    /**
     * 当没有内容可供显示时调用这个方法
     */
    public abstract void handleEmptyContent(View.OnClickListener listener);

    /**
     * 当正在加载数据时调用这个方法
     */
    public abstract void handleLoadingContent();

    /**
     * 当数据加载完成后调用这个方法
     */
    public abstract void handleLoadSuccessful();

    /**
     * 切换到失败
     *
     * @param errorMsg 失败提示的信息
     */
    public abstract void handleLoadFailure(String errorMsg);

    /**
     * 切换到失败状态
     *
     * @param errorMsg      失败的错误信息
     * @param retryListener 重试监听器
     */
    public abstract void handleLoadFailure(String errorMsg, View.OnClickListener retryListener);

    /**
     * 当加载失败后调用这个方法
     *
     * @param errorCode 失败的错误代码
     *                  用户可以根据这个错误代码来进行不同的失败提示
     */
    public abstract void handleLoadFailure(int errorCode);

    /**
     * 当加载失败后调用这个方法
     *
     * @param errorCode     失败的错误代码
     *                      用户可以根据这个错误代码来进行不同的失败提示
     * @param retryListener 点击监听器
     */
    public abstract void handleLoadFailure(int errorCode, View.OnClickListener retryListener);

    /**
     * 子类通过这个方法来通知当前的状态发生变化
     * 注意,这个方法不能通知 加载失败,加载失败请使用 带errorCode的方法
     *
     * @param status 当前的状态
     */
    public void onContentStatusChanged(int status) {
        switch (status) {
            case EMPTY_CONTENT:
                handleEmptyContent();
                break;
            case LOADING_CONTENT:
                handleLoadingContent();
                break;
            case LOADING_SUCCESSFUL:
                handleLoadSuccessful();
                break;
        }
    }

    /**
     * 子类通过这个方法来通知当前的状态发生变化
     *
     * @param status    当前的状态
     * @param errorCode 错误码
     */
    public void onContentStatusChanged(int status, int errorCode) {
        switch (status) {
            case EMPTY_CONTENT:
                handleEmptyContent();
                break;
            case LOADING_CONTENT:
                handleLoadingContent();
                break;
            case LOADING_SUCCESSFUL:
                handleLoadSuccessful();
                break;
            case LOADING_FAILURE:
                handleLoadFailure(errorCode);
                break;
        }
    }


    /**
     * 子类通过这个方法来通知当前的状态发生变化
     *
     * @param status   当前的状态
     * @param errorMsg 错误信息
     */
    public void onContentStatusChanged(int status, String errorMsg) {
        switch (status) {
            case EMPTY_CONTENT:
                handleEmptyContent();
                break;
            case LOADING_CONTENT:
                handleLoadingContent();
                break;
            case LOADING_SUCCESSFUL:
                handleLoadSuccessful();
                break;
            case LOADING_FAILURE:
                handleLoadFailure(errorMsg);
                break;
        }
    }


    /**
     * 子类通过这个方法来通知当前的状态发生变化
     *
     * @param status        当前的状态
     * @param errorMsg      错误信息
     * @param retryListener 点击重试监听器
     */
    public void onContentStatusChanged(int status, String errorMsg, View.OnClickListener retryListener) {
        switch (status) {
            case EMPTY_CONTENT:
                handleEmptyContent();
                break;
            case LOADING_CONTENT:
                handleLoadingContent();
                break;
            case LOADING_SUCCESSFUL:
                handleLoadSuccessful();
                break;
            case LOADING_FAILURE:
                handleLoadFailure(errorMsg, retryListener);
                break;
        }
    }


    /**
     * 子类通过这个方法来通知当前的状态发生变化
     *
     * @param status        当前的状态
     * @param errorCode      错误代码
     * @param retryListener 点击重试监听器
     */
    public void onContentStatusChanged(int status, int errorCode, View.OnClickListener retryListener) {
        switch (status) {
            case EMPTY_CONTENT:
                handleEmptyContent();
                break;
            case LOADING_CONTENT:
                handleLoadingContent();
                break;
            case LOADING_SUCCESSFUL:
                handleLoadSuccessful();
                break;
            case LOADING_FAILURE:
                handleLoadFailure(errorCode, retryListener);
                break;
        }
    }
}
