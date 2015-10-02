package com.lovely3x.common.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;



import com.lovely3x.common.utils.ArrayUtils;
import com.lovely3x.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 简化BaseAdapter
 *
 * @param <T>
 */
public abstract class ListAdapter<T> extends BaseAdapter {

    public static final String TAG = "ListAdapter";

    /**
     * 条目是否可以被点击
     */
    private boolean itemClickable;

    /**
     * 上下文资源访问对象
     */
    protected Context mContext;

    /**
     * 数据集合
     */
    protected List<T> datas;


    protected LayoutInflater mLayoutInflater;


    /**
     * 条目点击监听器
     */
    protected OnItemClickedListener<T> mOnItemClickedListener;

    /**
     * 通过一个list集合来创建适配器
     *
     * @param datas   数据集合
     * @param context 上下文对象
     */
    public ListAdapter(List<T> datas, Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        setData(datas);
    }
    /**
     * 通过一个数组来创建adapter,使用默认的图片包装器
     *
     * @param datas   需要显示的数据集合
     * @param context 上下文对象
     */
    public ListAdapter(final T[] datas, Context context) {
        this(ArrayUtils.ArrayToList(datas), context);
    }

    /**
     * 将数据添加数据集合的头部
     *
     * @param data 需要添加的数据
     */
    public void addDataToFirst(List<T> data) {
        if (data == null)
            return;
        if (this.datas == null) {
            setData(data);
        } else {
            this.datas.addAll(0, data);
        }
        notifyDataSetChanged();
    }

    /**
     * 从适配器中删除一一个元素
     *
     * @param t 需要删除的元素
     * @return 是否删除成功
     */
    public boolean delete(T t) {
        if (t == null || datas == null)
            return false;
        boolean result = datas.remove(t);
        notifyDataSetChanged();
        return result;
    }

    /**
     * @return 返回当前条目是否可以被点击
     */
    public boolean isItemClickable() {
        return itemClickable;
    }

    /**
     * 设置条目是否可被点击
     * 如果设置不可以点击,那么将不会设置监听器到条目视图上面
     *
     * @param itemClickable 是否可以点击
     */
    public void setItemClickable(boolean itemClickable) {
        this.itemClickable = itemClickable;
    }

    /**
     * 将数据添加到末尾
     *
     * @param data 需要添加的数据
     */
    public void addDataToLast(List<T> data) {
        if (data == null)
            return;
        if (this.datas == null) {
            setData(data);
        } else {
            this.datas.addAll(data);
        }
        notifyDataSetChanged();
    }


    /**
     * 将本次的数据添加到原有数据的末尾
     *
     * @param data 需要添加的数据
     */
    public void addDataToLast(T data) {
        if (data != null) {
            this.datas.add(data);
        }
        notifyDataSetChanged();
    }

    /**
     * 设置数据,原本的数据将会被清空
     *
     * @param datas 需要设置的数据
     */
    public void setData(final List<T> datas) {
        this.datas = (datas == null ? new ArrayList<T>() : datas);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        ViewUtils.mainThreaderCheck();
        super.notifyDataSetChanged();
    }

    /**
     * 清除掉当前适配器中的数据
     */
    public void clear() {
        if (datas != null) {
            datas.clear();
            notifyDataSetChanged();
        }
    }

    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 获取上下文对象
     */
    protected Context getContext() {
        return this.mContext;
    }

    /**
     * @return 获取数据集合
     */
    public List<T> getDatas() {
        return datas;
    }

    /**
     * 设置条目点击监听器
     *
     * @param t 需要设置的监听器
     */
    public void setmOnItemClickedListener(OnItemClickedListener<T> t) {
        this.mOnItemClickedListener = t;
    }

    /**
     * @return 获取视图充气机
     */
    protected LayoutInflater getLayoutInflater() {
        if (this.mLayoutInflater == null) {
            this.mLayoutInflater = LayoutInflater.from(mContext);
        }
        return mLayoutInflater;
    }

    /**
     *
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder holder;
        if (convertView == null) {
            holder = createViewHolder(position, parent);
            holder.mRootView.setTag(holder);
        } else {
            holder = (BaseViewHolder) convertView.getTag();
        }
        if (itemClickable) bindItemClickListener(holder.mRootView, position);
        handleData(position, holder);
        return holder.mRootView;
    }

    /**
     * 为条目设置监听器
     *
     * @param itemView 需要绑定监听器的根视图
     * @param position 需呀绑定监听器的位置
     */
    private void bindItemClickListener(View itemView, final int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null)
                    mOnItemClickedListener.onClicked(position, datas.get(position));
            }
        });
    }

    /**
     * 回调这个方法创建一个viewHolder
     *
     * @param position 当前需要创建视图的位置
     * @param parent   parent 咯
     * @return 创建的viewHolder
     */
    protected abstract
    @NonNull
    BaseViewHolder createViewHolder(int position, ViewGroup parent);

    /**
     * 通过这个方法用户将数据绑定到holder上面去
     *
     * @param position 当前的位置
     * @param holder   holder
     */
    protected abstract void handleData(int position, @NonNull BaseViewHolder holder);



    /**
     * list 条目点击监听器
     */
    public interface OnItemClickedListener<T> {

        /**
         * 当条目被点击后执行
         *
         * @param t        被点击的条目的数据
         * @param position 被点击的条目的位置
         */
        void onClicked(int position, T t);
    }
}
