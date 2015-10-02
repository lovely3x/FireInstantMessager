
package com.lovely3x.common.image.picker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.lovely3x.common.R;
import com.lovely3x.common.activities.DefaultEmptyContentTipActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePickerGridDetailActivity extends DefaultEmptyContentTipActivity implements View.OnClickListener, ImageDetailsPickerAdapter.SingleOnCheckedListener {

    public static final String KEY_BUNDLE_RESULT_IMAGE_PATH = "KEY_BUNDLE_RESULT_IMAGE_PATH";

    private static final String KEY_CHECKED_LIST = "key.checked.list";


    private ImageDetailsPickerAdapter mGridViewAdapter = null;

    private List<ImageItem> mGridListData = null;

    private GridView commonImagePickerDetailGridView;


    private boolean mMultiChoiceModel;

    private TextView mTvDone;


    @Override
    protected int getContentView() {
        return R.layout.activity_common_image_picker_detail;
    }

    @Override
    protected void onViewInitialized() {
        setTitle(R.string.title_image_picker);
        //多选模式下,添加右边的选择按钮
        if (mMultiChoiceModel) {
            addRightYesBtn();
            mTvDone.setOnClickListener(this);
        }
        mGridViewAdapter = new ImageDetailsPickerAdapter(null, mActivity, ImageLoader.getInstance());
        mGridViewAdapter.setData(mGridListData);
        commonImagePickerDetailGridView.setAdapter(mGridViewAdapter);
        mGridViewAdapter.setCheckModel(mMultiChoiceModel);
        mGridViewAdapter.setSingleOnCheckedListener(this);
    }

    /**
     * 在右边添加对勾按钮
     */
    protected void addRightYesBtn() {
        this.mTvDone = addRightBtn(R.string.adcc_done, R.id.tv_activity_image_picker_done);
    }

    @Override
    protected void initViews() {
        commonImagePickerDetailGridView = (GridView) findViewById(R.id.common_image_picker_detail_grid_view);
    }

    @Override
    protected void onInitExtras(Bundle bundle) {
        mGridListData = bundle.getParcelableArrayList(ImagePickerListActivity.KEY_BUNDLE_ALBUM_PATH);
        String title = bundle.getString(ImagePickerListActivity.KEY_BUNDLE_ALBUM_NAME);
        this.mMultiChoiceModel = bundle.getBoolean(ImagePickerListActivity.KEY_CHOICE_MODEL);
        setTitle(title);
    }

    @Override
    public void restoreInstanceOnCreateBefore(@NonNull Bundle savedInstance) {

    }

    @Override
    public void restoreInstanceOnCreateAfter(@NonNull Bundle savedInstance) {
        ArrayList<Integer> checkedList = savedInstance.getIntegerArrayList(KEY_CHECKED_LIST);
        if (mGridViewAdapter != null) {
            mGridViewAdapter.setItemChecked(checkedList);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (mGridViewAdapter != null) {
            SparseBooleanArray checkArray = mGridViewAdapter.getCheckArray();
            ArrayList<Integer> checkedArray = new ArrayList<>();
            final int len = checkArray.size();
            for (int i = 0; i < len; i++) {
                int position = checkArray.keyAt(i);
                if (checkArray.get(position)) {
                    checkedArray.add(position);
                }
            }
            outState.putIntegerArrayList(KEY_CHECKED_LIST, checkedArray);
        }
    }

    @Override
    public void handleLoadFailure(String errorMsg) {

    }

    @Override
    public void handleLoadFailure(String errorMsg, View.OnClickListener retryListener) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_activity_image_picker_done) {//完成
            Intent intent = new Intent();
            SparseBooleanArray checkedArray = mGridViewAdapter.getCheckArray();
            ArrayList<String> arrayList = new ArrayList<>();
            final int len = checkedArray.size();
            for (int i = 0; i < len; i++) {
                int position = checkedArray.keyAt(i);
                if (checkedArray.get(position)) {
                    ImageItem data = mGridViewAdapter.getDatas().get(position);
                    arrayList.add(data.getImagePath());
                }
            }
            if (mMultiChoiceModel) {//多选模式下
                intent.putStringArrayListExtra("data", arrayList);
            } else {//
                if (!arrayList.isEmpty()) {
                    intent.setData(Uri.fromFile(new File(arrayList.get(0))));
                }
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onSingleChecked(int position, boolean isChecked) {
        Intent intent = new Intent();
        intent.setData(Uri.fromFile(new File(mGridViewAdapter.getDatas().get(0).getImagePath())));
        setResult(RESULT_OK, intent);
        finish();

    }
}
