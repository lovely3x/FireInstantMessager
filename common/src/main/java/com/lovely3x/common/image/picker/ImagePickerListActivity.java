/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lovely3x.common.image.picker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.lovely3x.common.R;
import com.lovely3x.common.activities.DefaultEmptyContentTipActivity;
import com.lovely3x.common.activities.TitleActivity;
import com.lovely3x.common.adapter.ListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 图片列表
 */
public class ImagePickerListActivity extends DefaultEmptyContentTipActivity implements AdapterView.OnItemClickListener {

    private static final int IMAGE_PICKER_DETAIL_REQUEST_CODE = 200;

    public static final String KEY_BUNDLE_ALBUM_PATH = "KEY_BUNDLE_ALBUM_PATH";

    public static final String KEY_BUNDLE_ALBUM_NAME = "KEY_BUNDLE_ALBUM_NAME";

    /**
     * 传递选择模式的key
     */
    public static final String KEY_CHOICE_MODEL = "key.choice.model";


    /**
     * 选择模式 默认单选模式
     */
    public String choiceMode = MULTIPLE_MODEL;

    /**
     * 单选模式
     */
    public static final String SINGLE_MODEL = "choice.model.single";

    /**
     * 多选模式
     */
    public static final String MULTIPLE_MODEL = "choice.model.multiple";


    /**
     * 返回数据类型
     */
    private int returnType = RETURN_DATA_TYPE_URI;

    /**
     * 返回数据类型 URI
     */
    public static final int RETURN_DATA_TYPE_URI = 0X1;




    private ListAdapter<ImageBucket> mListViewAdapter = null;

    private AsyncTask<Void, Void, List<ImageBucket>> mAlbumLoadTask = null;

    private ListView mImagePickerListView;

    @Override
    protected void onInitExtras(Bundle bundle) {
        //获取选择模式
        choiceMode = bundle.getString(KEY_CHOICE_MODEL, SINGLE_MODEL);
    }

    /**
     * 加载图片
     */
    private void loadImage() {
        mAlbumLoadTask = new AsyncTask<Void, Void, List<ImageBucket>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                onContentStatusChanged(LOADING_CONTENT);
                ImagePickerHelper.getHelper().init(mActivity);
            }

            @Override
            protected List<ImageBucket> doInBackground(Void... params) {
                return ImagePickerHelper.getHelper().getImagesBucketList();
            }

            @Override
            protected void onPostExecute(List<ImageBucket> list) {
                onContentStatusChanged(LOADING_SUCCESSFUL);
                mListViewAdapter.setData(list);
            }
        };

        mAlbumLoadTask.execute();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mAlbumLoadTask && !mAlbumLoadTask.isCancelled()) {
            mAlbumLoadTask.cancel(true);
            mAlbumLoadTask = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == IMAGE_PICKER_DETAIL_REQUEST_CODE) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_common_image_picker_list;
    }

    @Override
    protected void onViewInitialized() {
        setTitle(getResources().getString(R.string.title_image_picker));
        mListViewAdapter = new PickerAdapter(null, this, ImageLoader.getInstance());
        mImagePickerListView.setAdapter(mListViewAdapter);
        mImagePickerListView.setOnItemClickListener(this);
        loadImage();
    }

    @Override
    protected void initViews() {
        mImagePickerListView = (ListView) findViewById(R.id.common_image_picker_list_view);
    }

    @Override
    public void restoreInstanceOnCreateBefore(@NonNull Bundle savedInstance) {

    }

    @Override
    public void restoreInstanceOnCreateAfter(@NonNull Bundle savedInstance) {

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageBucket item = mListViewAdapter.getItem(position);
        Bundle extras = new Bundle();
        extras.putParcelableArrayList(KEY_BUNDLE_ALBUM_PATH, item.bucketList);
        extras.putString(KEY_BUNDLE_ALBUM_NAME, item.bucketName);
        extras.putBoolean(KEY_CHOICE_MODEL, choiceMode.equals(MULTIPLE_MODEL));
        launchActivityForResult(ImagePickerGridDetailActivity.class, IMAGE_PICKER_DETAIL_REQUEST_CODE, extras);
    }
}
