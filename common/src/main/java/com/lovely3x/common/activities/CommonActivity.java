package com.lovely3x.common.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lovely3x.common.R;
import com.lovely3x.common.image.camera.CameraActivity;
import com.lovely3x.common.image.crop.CropImageActivity;
import com.lovely3x.common.image.displayer.ImgBrowserPagerAdapter;
import com.lovely3x.common.image.picker.ImagePickerListActivity;
import com.lovely3x.common.utils.ALog;
import com.lovely3x.common.utils.StorageUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用activity界面
 * Created by lovely3x on 15-8-15.
 */
public abstract class CommonActivity extends AppCompatActivity {
    private static final java.lang.String TAG = "CommonActivity";

    /**
     * 默认的图片裁切大小
     */
    public static final int DEFAULT_IMAGE_CROP_WIDTH_AND_HEIGHT = 800;
    /**
     * 保存图片选择对话框是否正在显示的key
     */
    private static final String KEY_CHOICE_IMG_DIALOG_IS_SHOWING = "key.choice.img.dialog.is.showing";
    /**
     * 保存图片选择对话框请求id的key
     */
    private static final String KEY_CHOICE_IMG_DIALOG_REQUEST_ID = "key.choice.img.dialog.request.id";
    /**
     * 保存图片选择对话框标题的key
     */
    private static final String KEY_CHOICE_IMG_DIALOG_TITLE = "key.choice.img.dialog.title";
    /**
     * 保存图片选择对话框显示的内容的key
     */
    private static final String KEY_CHOICE_IMG_DIALOG_MESSAGE = "key.choice.img.dialog.message";
    /**
     * 保存请求码集合的key
     */
    private static final String KEY_REQUEST_CODE_TABLE = "key.request.code.table";

    protected Context mActivity;
    /**
     * 记录请求值的code表
     */
    private SparseArray<Type> requestCodeTable = new SparseArray<>();
    /**
     * 图片选择框是否正在显示
     */
    private boolean mChoiceImgDialogIsShowing;
    /**
     * 图片选择的请求码
     */
    private int mChoiceImgRequestCode = -1;
    /**
     * 图片选择的对话框标题
     */
    private String mChoiceImgDialogTitle;
    /**
     * 图片选择的对话框内容
     */
    private String mChoiceImgDialogMessage;
    /**
     * 图片选择对话框
     */
    private AlertDialog mChoiceImgDialog;
    private ViewPager imgBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        android.app.ActivityManager activityManager = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningAppProcessInfo> process = activityManager.getRunningAppProcesses();
        activityManager.getRunningTasks(2);

        ActivityManager.launchReply();
        ActivityManager.updateState(this, ActivityManager.ACTIVITY_STATE_ON_CREATE);

        mActivity = this;
        if (ActivityManager.getActivities().size() == 1) {//如果仅仅存在一个
            Activity act = ActivityManager.getActivities().get(0);
            try {
                ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_ACTIVITIES);
                if (act != null && info.name.equals(act.getClass().getName())) {
                    entryForeground();
                }
            } catch (PackageManager.NameNotFoundException e) {
                ALog.e(TAG, e);
            }

        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            onInitExtras(extras);
        }
        if (savedInstanceState != null) restoreInstance(savedInstanceState);
        if (mChoiceImgDialogIsShowing && mChoiceImgRequestCode != -1) {
            mChoiceImgDialogIsShowing = false;
            showChoiceImageDialog(mChoiceImgRequestCode, mChoiceImgDialogTitle, mChoiceImgDialogMessage);
        }
    }

    private void restoreInstance(Bundle savedInstanceState) {
        mChoiceImgDialogIsShowing = savedInstanceState.getBoolean(KEY_CHOICE_IMG_DIALOG_IS_SHOWING);
        mChoiceImgRequestCode = savedInstanceState.getInt(KEY_CHOICE_IMG_DIALOG_REQUEST_ID);
        mChoiceImgDialogTitle = savedInstanceState.getString(KEY_CHOICE_IMG_DIALOG_TITLE);
        mChoiceImgDialogMessage = savedInstanceState.getString(KEY_CHOICE_IMG_DIALOG_MESSAGE);
        requestCodeTable = savedInstanceState.getSparseParcelableArray(KEY_REQUEST_CODE_TABLE);
        requestCodeTable = (requestCodeTable == null) ? new SparseArray<Type>() : requestCodeTable;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityManager.updateState(this, ActivityManager.ACTIVITY_STATE_ON_START);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (ActivityManager.isFromBackgroundToForeground(this)) {
            entryForeground();
        }
        ActivityManager.updateState(this, ActivityManager.ACTIVITY_STATE_ON_RESTART);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityManager.updateState(this, ActivityManager.ACTIVITY_STATE_ON_RESUME);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager.updateState(this, ActivityManager.ACTIVITY_STATE_ON_PAUSE);
    }

    @Override
    protected void onDestroy() {
        if (mChoiceImgDialog != null && mChoiceImgDialog.isShowing()) {
            mChoiceImgDialog.cancel();
            mChoiceImgDialog = null;
        }
        super.onDestroy();
        ActivityManager.updateState(this, ActivityManager.ACTIVITY_STATE_ON_DESTROY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ActivityManager.updateState(this, ActivityManager.ACTIVITY_STATE_ON_STOP);

    }


    /**
     * 显示图片
     *
     * @param url            需要显示的url
     * @param loadingImg     加载中的图片
     * @param loadFailureImg 加载失败的图片
     */
    public void showImg(String url, Bitmap loadingImg, Bitmap loadFailureImg) {
        showImg(url, ImgBrowserPagerAdapter.IMG_SOURCE_TYPE_URL, loadingImg, loadFailureImg);
    }

    /**
     * 显示图片
     *
     * @param url            需要显示的uri
     * @param contentType    需要显示的内容的类型,目前支持
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_ASSERT} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_BITMAP} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_FILE} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_URL}
     * @param loadingImg     加载中的图片
     * @param loadFailureImg 加载失败显示的图片
     */
    public void showImg(String url, int contentType, Bitmap loadingImg, Bitmap loadFailureImg) {
        ArrayList<String> list = new ArrayList<>();
        list.add(url);
        showImgs(list, contentType, loadingImg, loadFailureImg);
    }

    /**
     * 显示一组图片
     *
     * @param urls              需要显示的uri
     * @param contentType       需要显示的内容的类型,目前支持
     *                          {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_ASSERT} ,
     *                          {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_BITMAP} ,
     *                          {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_FILE} ,
     *                          {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_URL}
     * @param loadingImg        加载中显示的图片
     * @param loadingFailureImg 加载失败显示的图片
     */
    public void showImgs(List<String> urls, int contentType, Bitmap loadingImg, Bitmap loadingFailureImg) {
        showImgs(urls, contentType, true, loadingImg, loadingFailureImg, 0);
    }

    /**
     * 显示一组图片
     *
     * @param urls              需要显示的uri
     * @param contentType       需要显示的内容的类型,目前支持
     *                          {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_ASSERT} ,
     *                          {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_BITMAP} ,
     *                          {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_FILE} ,
     *                          {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_URL}
     * @param loadingImg        加载中显示的图片
     * @param loadingFailureImg 加载失败显示的图片
     * @param select            当前选中的条目
     */
    public void showImgs(List<String> urls, int contentType, Bitmap loadingImg, Bitmap loadingFailureImg, int select) {
        showImgs(urls, contentType, true, loadingImg, loadingFailureImg, select);
    }

    /**
     * 显示图片
     *
     * @param url            需要显示的图片地址
     * @param loadingImg     加载中的图片资源
     * @param loadFailureImg 记载失败的图片资源
     */
    public void showImg(String url, @DrawableRes int loadingImg, @DrawableRes int loadFailureImg) {
        showImg(url, ImgBrowserPagerAdapter.IMG_SOURCE_TYPE_URL, loadingImg, loadFailureImg);
    }

    /**
     * 显示图片
     *
     * @param bm 需要显示的bitmap对象
     */
    public void showImg(Bitmap bm) {
        ArrayList<Bitmap> bms = new ArrayList<>();
        bms.add(bm);
        showImgs(bms, R.drawable.icon_loading_failure);
    }

    public void showImg(String url) {
        showImg(url, ImgBrowserPagerAdapter.IMG_SOURCE_TYPE_URL, R.drawable.icon_loading, R.drawable.icon_loading_failure);
    }

    /**
     * 显示图片
     *
     * @param uri            需要显示的uri
     * @param contentType    需要显示的内容的类型,目前支持
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_ASSERT} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_BITMAP} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_FILE} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_URL}
     * @param loadingImg     加载中的图片资源
     * @param loadFailureImg 加载失败显示的图片资源
     */
    public void showImg(String uri, int contentType, @DrawableRes int loadingImg, @DrawableRes int loadFailureImg) {
        ArrayList<String> list = new ArrayList<>();
        list.add(uri);
        showImgs(list, contentType, true, loadingImg, loadFailureImg, 0);
    }

    /**
     * 显示一组图片
     *
     * @param urls           需要显示的图片网络地址
     * @param loadingImg     加载中显示的图片
     * @param loadFailureImg 加载失败显示的图片
     */
    public void showImgs(List<String> urls, Bitmap loadingImg, Bitmap loadFailureImg) {
        showImgs(urls, ImgBrowserPagerAdapter.IMG_SOURCE_TYPE_URL, loadingImg, loadFailureImg);
    }


    /**
     * /**
     * 显示一组图片
     *
     * @param uris           需要显示的图片的uri's
     * @param contentType    需要显示的内容的类型,目前支持
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_ASSERT} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_BITMAP} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_FILE} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_URL}
     * @param loadingImg     加载中显示的图片资源
     * @param loadFailureImg 加载失败显示的图片资源
     */
    public void showImgs(List<String> uris, int contentType, @DrawableRes int loadingImg, @DrawableRes int loadFailureImg) {
        showImgs(uris, contentType, true, loadingImg, loadFailureImg, 0);
    }

    /**
     * 显示一组图片
     *
     * @param uris           需要显示的图片的uri's
     * @param contentType    需要显示的内容的类型,目前支持
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_ASSERT} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_BITMAP} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_FILE} ,
     *                       {@link ImgBrowserPagerAdapter#IMG_SOURCE_TYPE_URL}
     * @param loadingImg     加载中显示的图片资源
     * @param loadFailureImg 加载失败显示的图片资源
     * @param select         当前选中的条目的下标
     */
    public void showImgs(List<String> uris, int contentType, @DrawableRes int loadingImg, @DrawableRes int loadFailureImg, int select) {
        showImgs(uris, contentType, true, loadingImg, loadFailureImg, select);
    }

    /**
     * 显示一组图片
     *
     * @param bms           需要显示的bitmap对象
     * @param loafailureImg 加载失败显示的图片
     */
    public void showImgs(List<Bitmap> bms, Bitmap loafailureImg) {
        showImgs(bms, ImgBrowserPagerAdapter.IMG_SOURCE_TYPE_BITMAP, true, R.drawable.icon_loading, R.drawable.icon_loading_failure, 0);
    }

    /**
     * 显示一组图片
     *
     * @param bms            需要显示的bitmap对象
     * @param loadFailureImg 加载失败显示的图片
     * @param select         当前选中的条目下标
     */
    public void showImgs(List<Bitmap> bms, Bitmap loadFailureImg, int select) {
        showImgs(bms, ImgBrowserPagerAdapter.IMG_SOURCE_TYPE_BITMAP, true, R.drawable.icon_loading, R.drawable.icon_loading_failure, select);
    }

    /**
     * 显示一组图片
     *
     * @param bms            需要显示的图片
     * @param loadFailureImg 加载失败显示的图片资源
     */
    public void showImgs(List<Bitmap> bms, @DrawableRes int loadFailureImg) {
        showImgs(bms, ImgBrowserPagerAdapter.IMG_SOURCE_TYPE_BITMAP, true, R.drawable.icon_loading, R.drawable.icon_loading_failure, 0);
    }


    /**
     * 显示一组图片
     *
     * @param bms            需要显示的图片
     * @param loadFailureImg 加载失败显示的图片资源
     */
    public void showImgs(List<Bitmap> bms, @DrawableRes int loadFailureImg, int select) {
        showImgs(bms, ImgBrowserPagerAdapter.IMG_SOURCE_TYPE_BITMAP, true, R.drawable.icon_loading, R.drawable.icon_loading_failure, select);
    }


    /**
     * 显示一组图片
     * 子类完全可以自己重写实现
     *
     * @param imgs           需要喜爱那是的图片集合
     * @param contentType    需要显示的图片的类型
     * @param hasAim         是否使用动画(至于什么动画,自由发挥)
     * @param loadingImg     加载中图片
     * @param loadFailureImg 加载失败图片
     * @param select         选中的下标
     */
    protected void showImgs(List<? extends Object> imgs, int contentType,
                            boolean hasAim, @DrawableRes int loadingImg,
                            @DrawableRes int loadFailureImg, int select) {
        coverView();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imgBrowser = new ViewPager(this);
        imgBrowser.setBackgroundColor(0xFF000000);
        ImgBrowserPagerAdapter adapter = new ImgBrowserPagerAdapter(this, imgs, contentType, hasAim, loadingImg, loadFailureImg);
        adapter.setOnItemClicked(new ImgBrowserPagerAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int position) {
                onImgClicked(position);
            }
        });
        imgBrowser.setAdapter(adapter);
        imgBrowser.setCurrentItem(select);
        getContentViewFrame().addView(imgBrowser, lp);
    }


    /**
     * 当某一张图片被点击后执行
     */
    protected void onImgClicked(int position) {
        ViewGroup contentFrame = getContentViewFrame();
        contentFrame.removeView(imgBrowser);
        int count = contentFrame.getChildCount();
        for (int i = 0; i < count; i++) contentFrame.getChildAt(i).setVisibility(View.VISIBLE);
        imgBrowser = null;
    }


    /**
     * 显示一组图片
     * 子类完全可以自己重写实现
     *
     * @param imgs           需要喜爱那是的图片集合
     * @param contentType    需要显示的图片的类型
     * @param hasAim         是否使用动画(至于什么动画,自由发挥)
     * @param loadingImg     加载中图片
     * @param loadFailureImg 加载失败图片
     * @param select         默认选中的条目
     */
    protected void showImgs(List<? extends Object> imgs,
                            int contentType, boolean hasAim,
                            Bitmap loadingImg, Bitmap loadFailureImg, int select) {
        coverView();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imgBrowser = new ViewPager(this);
        ImgBrowserPagerAdapter adapter = new ImgBrowserPagerAdapter(this, imgs, contentType, hasAim, loadingImg, loadFailureImg);
        imgBrowser.setAdapter(adapter);
        imgBrowser.setCurrentItem(select);
        imgBrowser.setBackgroundColor(0xFF000000);
        getContentViewFrame().addView(imgBrowser, lp);
        adapter.setOnItemClicked(new ImgBrowserPagerAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int position) {
                onImgClicked(position);
            }
        });
    }

    protected ViewGroup getContentViewFrame() {
        ViewGroup vg = (ViewGroup) getWindow().getDecorView();
        ViewGroup contentViewFrame = (ViewGroup) ((ViewGroup) vg.getChildAt(0)).getChildAt(1);
        return contentViewFrame;
    }

    @Override
    public void onBackPressed() {
        if (imgBrowser == null) {
            super.onBackPressed();
        } else {
            onImgClicked(-1);
        }
    }

    /**
     * 覆盖当前正在显示的视图
     * 为显示图片浏览器做准备
     */
    protected void coverView() {
        ViewGroup contentViewFrame = getContentViewFrame();
        int count = contentViewFrame.getChildCount();
        for (int i = 0; i < count; i++)
            contentViewFrame.getChildAt(i).setVisibility(View.INVISIBLE);
    }


    /**
     * 从后台转入前台
     */
    protected void entryForeground() {
        /**
         * 突然想到一个问题
         * 是否可以通过activityManager 查询最近的activity信息达到判断程序前后台的这个目的
         */
        ALog.i(TAG, "from background entry to foreground.");
    }


    /**
     * 初始化附加值
     * 其他的activity
     *
     * @param bundle
     */
    protected void onInitExtras(@NonNull Bundle bundle) {

    }


    /**
     * 启动指定的界面
     *
     * @param compoundsClazz 需要启动的activity组件名
     * @param bundle         需要传递的数据
     *                       默认不清除其他的activity
     */
    public void launchActivity(Class<? extends Activity> compoundsClazz, Bundle bundle) {
        launchActivity(compoundsClazz, bundle, false);
    }


    /**
     * 启动指定的界面
     *
     * @param compoundsClazz         需要启动的activity组件名
     * @param bundle                 需要传递的数据
     * @param launchBeforeClearStack 启动之前先清除栈数据
     */
    public void launchActivity(Class<? extends Activity> compoundsClazz, Bundle bundle, boolean launchBeforeClearStack) {
        ActivityManager.launchActivity(this, compoundsClazz, bundle, launchBeforeClearStack);
    }

    /**
     * 启动指定的界面
     *
     * @param compoundsClazz 需要启动的activity组件名
     * @param params         需要传递的数据
     */
    public void launchActivity(Class<? extends Activity> compoundsClazz, Object... params) {
        launchActivity(compoundsClazz, buildBundle(params), false);
    }

    /**
     * 启动指定的界面
     *
     * @param compoundsClazz 需要启动的activity组件名
     */
    public void launchActivity(Class<? extends Activity> compoundsClazz) {
        launchActivity(compoundsClazz, null, false);
    }


    /**
     * 启动指定的界面
     *
     * @param compoundsClazz         需要启动的activity组件名
     * @param launchBeforeClearStack 启动之前先清除栈数据
     * @param arguments              需要传递的参数数组
     */
    public void launchActivity(Class<? extends Activity> compoundsClazz, boolean launchBeforeClearStack, Object... arguments) {
        launchActivity(compoundsClazz, buildBundle(arguments), launchBeforeClearStack);
    }

    /**
     * 启动一个activity 带请求码的
     *
     * @param compoundsClazz 需要启动的类
     * @param requestCode    请求码
     */
    public void launchActivityForResult(Class<? extends Activity> compoundsClazz, int requestCode) {
        launchActivityForResult(compoundsClazz, false, requestCode);
    }

    /**
     * 启动一个activity 带请求码的
     *
     * @param compoundsClazz 需要启动的类
     * @param requestCode    请求码
     */
    public void launchActivityForResult(Class<? extends Activity> compoundsClazz, boolean launchBeforeClearActivities, int requestCode) {
        launchActivityForResult(compoundsClazz, requestCode, launchBeforeClearActivities, new Bundle());
    }


    public void launchActivityForResult(Class<? extends Activity> compoundsClazz, int requestCode, Object... params) {
        launchActivityForResult(compoundsClazz, requestCode, buildBundle(params));
    }

    public void launchActivityForResult(Class<? extends Activity> compoundsClazz, int requestCode, Bundle bundle) {
        launchActivityForResult(compoundsClazz, requestCode, false, bundle);
    }

    public void launchActivityForResult(Class<? extends Activity> compoundsClazz, int requestCode, boolean launchBeforeClearActivities, Object... params) {
        launchActivityForResult(compoundsClazz, requestCode, launchBeforeClearActivities, buildBundle(params));
    }

    public void launchActivityForResult(Class<? extends Activity> compoundsClazz, int requestCode, boolean launchBeforeClearActivities, Bundle bundle) {
        ActivityManager.launchActivityForResult(this, compoundsClazz, bundle, launchBeforeClearActivities, requestCode);
    }

    /**
     * 构建bundle对象
     *
     * @param arguments 需要构建的参数
     * @return 构建好的bundle对象
     */
    protected Bundle buildBundle(Object... arguments) {
        if (arguments == null) return null;

        Bundle bundle = new Bundle();
        if (arguments.length % 2 == 0) {
            final int count = arguments.length / 2;
            for (int i = 0; i < count; i++) {
                String key = arguments[i * 2].toString();
                Object value = arguments[i * 2 + 1];
                if (key != null && value != null) {
                    if (value instanceof Byte) {
                        bundle.putByte(key, (Byte) value);
                    } else if (value instanceof Short) {
                        bundle.putShort(key, (Short) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(key, (Integer) value);
                    } else if (value instanceof Long) {
                        bundle.putLong(key, (Long) value);
                    } else if (value instanceof Float) {
                        bundle.putFloat(key, (Float) value);
                    } else if (value instanceof Double) {
                        bundle.putDouble(key, (Double) value);
                    } else if (value instanceof String) {
                        bundle.putString(key, (String) value);
                    } else if (value instanceof Bundle) {
                        bundle.putBundle(key, (Bundle) value);
                    } else if (value instanceof Parcelable) {
                        bundle.putParcelable(key, (Parcelable) value);
                    } else if (value instanceof Serializable) {
                        bundle.putSerializable(key, (Serializable) value);
                    } else {
                        ALog.e(TAG, "unsupported object");
                    }
                }
            }

        }
        return bundle;
    }

    /**
     * 显示图像选择对话框
     *
     * @param requestCode 请求码,用来标识不同的请求
     */
    public void showChoiceImageDialog(final int requestCode, String title, String msg) {
        if (requestCode == -1) throw new IllegalArgumentException("request code cant is -1");
        if (!mChoiceImgDialogIsShowing) {
            mChoiceImgDialogIsShowing = true;
            mChoiceImgRequestCode = requestCode;
            mChoiceImgDialogTitle = title;
            mChoiceImgDialogMessage = msg;
            mChoiceImgDialog = new AlertDialog.Builder(this)
                    .setNegativeButton(R.string.gallery, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Type type = requestCodeTable.get(requestCode);
                            if (type == null) {
                                type = new Type();
                                type.type = Type.TYPE_GALLERY;
                            }
                            if (type.type != Type.TYPE_GALLERY)
                                throw new RuntimeException("多次请求类型必须一致.");

                            type.times++;
                            type.requestCode = requestCode;
                            requestCodeTable.put(requestCode, type);
                            launchActivityForResult(ImagePickerListActivity.class, requestCode);
                        }
                    })
                    .setPositiveButton(R.string.camera, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Type type = requestCodeTable.get(requestCode);
                            if (type == null) {
                                type = new Type();
                                type.type = Type.TYPE_CAMERA;
                            }

                            if (type.type != Type.TYPE_CAMERA)
                                throw new RuntimeException("多次请求类型必须一致.");

                            type.times++;
                            type.requestCode = requestCode;
                            requestCodeTable.put(requestCode, type);
                            launchActivityForResult(CameraActivity.class, requestCode);
                        }
                    })
                    .setTitle(title).setMessage(msg)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //是在onDestroy处调用的
                            mChoiceImgDialogIsShowing = mChoiceImgDialog == null;
                            ALog.e(TAG, "onDismiss");
                        }
                    }).create();
            mChoiceImgDialog.show();
        }
    }

    /**
     * 显示图像选择对话框
     *
     * @param requestCode 请求码,用来标识不同的请求
     */
    public void showChoiceImageDialog(final int requestCode) {
        showChoiceImageDialog(requestCode,
                getString(R.string.alert),
                getString(R.string.do_you_want_to_select_images_from_where));
    }

    /**
     * 裁切图片
     *
     * @param inUri       输入地址
     * @param outURi      输出地址
     * @param aspectX     x比重
     * @param aspectY     y比重
     * @param width       宽度
     * @param height      高度
     * @param requestCode 请求码
     */
    public void cropImage(Uri inUri, Uri outURi, int aspectX, int aspectY, int width, int height, int requestCode) {
        Type type = requestCodeTable.get(requestCode);
        if (type == null) {
            type = new Type();
            type.type = Type.TYPE_CROP;
        }

        if (type.type != Type.TYPE_CROP) throw new RuntimeException("多次请求类型必须一致.");

        type.times++;
        type.requestCode = requestCode;
        requestCodeTable.put(requestCode, type);

        Intent intent = new Intent(this, CropImageActivity.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outURi);
        intent.setData(inUri);
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("noFaceDetection", true);

        startActivityForResult(intent, requestCode);
    }

    /**
     * 裁切图片
     *
     * @param inUri       输入地址
     * @param outURi      输出地址
     * @param width       宽度
     * @param height      高度
     * @param requestCode 请求码
     */
    public void cropImage(Uri inUri, Uri outURi, int width, int height, int requestCode) {
        cropImage(inUri, outURi, 1, 1, width, height, requestCode);
    }

    /**
     * 显示toast
     *
     * @param stringResId 资源id
     */
    public void showToast(@StringRes int stringResId) {
        showToast(getString(stringResId));
    }

    /**
     * 显示toast
     *
     * @param string 显示toast
     */
    public void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }


    /**
     * 当图片被裁切完成后执行
     *
     * @param imgUri      裁切完成的图片保存的地址
     * @param requestCode 发起照片裁切的请求码
     */
    public void onImageCropped(Uri imgUri, int requestCode) {
        Log.e(TAG, "onImageCropped -> " + imgUri);
    }

    /**
     * 当从相册中选取照片后回调
     *
     * @param uri         照片保存的地址
     * @param requestCode 发起照片选择的请求码
     */
    public void onGalleryPicked(Uri uri, int requestCode) {
        cropImage(uri, Uri.fromFile(new File(
                        StorageUtils.getApplicationCacheDir(this),
                        String.valueOf(System.currentTimeMillis()).concat(".png"))),
                DEFAULT_IMAGE_CROP_WIDTH_AND_HEIGHT, DEFAULT_IMAGE_CROP_WIDTH_AND_HEIGHT, requestCode);
    }

    /**
     * 当使用相机拍摄照片后回调
     *
     * @param uri         照片拍摄后保存的地址
     * @param requestCode 发起拍照获取图片的请求码
     */
    public void onCameraTaken(Uri uri, int requestCode) {
        cropImage(uri, Uri.fromFile(new File(
                        StorageUtils.getApplicationCacheDir(this),
                        String.valueOf(System.currentTimeMillis()).concat(".png"))),
                DEFAULT_IMAGE_CROP_WIDTH_AND_HEIGHT, DEFAULT_IMAGE_CROP_WIDTH_AND_HEIGHT, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Type requestCodeType = requestCodeTable.get(requestCode);
        if (requestCodeType != null) {
            if (requestCodeType.times > 0) {
                requestCodeType.times--;
                if (requestCodeType.times <= 0) requestCodeTable.remove(requestCode);

                if (requestCodeType.type == Type.TYPE_CROP) {//裁切图片
                    if (data != null && data.getAction() != null) {
                        onImageCropped(Uri.parse((data.getAction())), requestCode);
                    }
                } else if (requestCodeType.type == Type.TYPE_GALLERY) {//从相册选择图片
                    if (data != null && data.getData() != null) {
                        onGalleryPicked(data.getData(), requestCode);
                    }
                } else if (requestCodeType.type == Type.TYPE_CAMERA) {
                    if (data != null && data.getData() != null) {
                        onCameraTaken(data.getData(), requestCode);
                    }
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_CHOICE_IMG_DIALOG_IS_SHOWING, mChoiceImgDialogIsShowing);
        outState.putInt(KEY_CHOICE_IMG_DIALOG_REQUEST_ID, mChoiceImgRequestCode);
        outState.putString(KEY_CHOICE_IMG_DIALOG_TITLE, mChoiceImgDialogTitle);
        outState.putString(KEY_CHOICE_IMG_DIALOG_MESSAGE, mChoiceImgDialogMessage);
        outState.putSparseParcelableArray(KEY_REQUEST_CODE_TABLE, requestCodeTable);
    }

    /**
     * 发出申请的类型
     */
    public static class Type implements Parcelable {
        /**
         * 裁切图片
         */
        public static final int TYPE_CROP = 0x1;
        /**
         * 从相册选择图片
         */
        public static final int TYPE_GALLERY = 0x2;
        /**
         * 从相机选择
         */
        public static final int TYPE_CAMERA = 0x3;

        /**
         * 请求码
         */
        public int requestCode;

        /**
         * 次数
         */
        public int times;

        /**
         * 类型
         */
        public int type = ~0;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.requestCode);
            dest.writeInt(this.times);
            dest.writeInt(this.type);
        }

        public Type() {
        }

        protected Type(Parcel in) {
            this.requestCode = in.readInt();
            this.times = in.readInt();
            this.type = in.readInt();
        }

        public static final Parcelable.Creator<Type> CREATOR = new Parcelable.Creator<Type>() {
            public Type createFromParcel(Parcel source) {
                return new Type(source);
            }

            public Type[] newArray(int size) {
                return new Type[size];
            }
        };
    }
}
