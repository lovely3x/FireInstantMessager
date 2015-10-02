package com.lovely3x.common.animations;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;


/**
 * 位移动画辅助类
 * 一个我经常使用的动画位移动画辅助类
 * Created by lovely3x on 15-9-29.
 */
public class TranslationFromBottom implements View.OnClickListener {

    /**
     * 背景视图,在动画播放时对这个视图进行背景色的变化
     */
    private final View mBackgroundView;
    /**
     * 内容视图,在动画播放时的对这个视图进行位移
     */
    private final View mContentView;
    /**
     * 用来包含子背景视图和内容视图的容器视图
     * 在动画播放时会对他做可见不可见的处理
     */
    private final View mContainerView;

    /**
     * 背景开始的颜色
     */
    private int startColor = 0x00000000;

    /**
     * 背景停止的颜色
     */
    private int stopColor = 0x77000000;

    /**
     * 动画播放时长
     */
    private int mDuration = 300;


    /**
     *
     * @param containerView 内容视图和背景视图容器
     * @param backgroundView 背景市图
     * @param contentView 内容视图
     */
    public TranslationFromBottom(View containerView, View backgroundView, View contentView) {
        this.mBackgroundView = backgroundView;
        this.mContentView = contentView;
        this.mContainerView = containerView;
        mContainerView.setClickable(false);
    }

    /**
     * 播放显示动画
     *  @param duration 动画播放时间
     */
    public void out(int duration) {
        //背景色动画
        ObjectAnimator backgroundAnim = ObjectAnimator.ofInt(mBackgroundView, "backgroundColor", startColor, stopColor);
        backgroundAnim.setEvaluator(new AlphaEvaluator());
        //位移动画
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mContentView, "translationY", mContentView.getMeasuredHeight(), 0);
        AnimatorSet as = new AnimatorSet();
        as.playTogether(backgroundAnim, translationY);
        as.setDuration(duration);
        as.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mContainerView.setVisibility(View.VISIBLE);
                mContainerView.setClickable(true);
                mContainerView.setOnClickListener(TranslationFromBottom.this);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        as.start();
    }

    /**
     * 关闭动画
     * @param duration 动画播放时间
     */
    public void in(int duration) {
        this.mDuration = duration;
        //背景色动画
        ObjectAnimator backgroundAnim = ObjectAnimator.ofInt(mBackgroundView, "backgroundColor", stopColor, startColor);
        backgroundAnim.setEvaluator(new AlphaEvaluator());
        //位移动画
        ObjectAnimator translationY = ObjectAnimator.ofFloat(mContentView, "translationY", 0, mContentView.getMeasuredHeight());
        AnimatorSet as = new AnimatorSet();
        as.playTogether(backgroundAnim, translationY);
        as.setDuration(duration);
        as.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mContainerView.setVisibility(View.INVISIBLE);
                mContainerView.setClickable(false);
                mContainerView.setOnClickListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        as.start();
    }
    /**
     * 关闭动画
     */
    public void in() {
        in(300);
    }

    /**
     * 播放显示动画
     */
    public void out() {
        out(300);
    }

    @Override
    public void onClick(View v) {
        in(mDuration);
    }
}
