package com.xiaolei.datepicker;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <DataType>   传入的数据类型
 * @param <viewHolder> ViewHoler的类型
 * @author 肖蕾
 */
public abstract class BaseAdapter<DataType, viewHolder extends BaseAdapter.Holder> extends android.widget.BaseAdapter
{
    /**
     * 保存的数据
     */
    private List<DataType> list = new ArrayList<DataType>();
    private List<DataType> outer_list;

    public BaseAdapter(List<DataType> list)
    {
        this.outer_list = list;
        this.list.addAll(outer_list);
    }

    @Override
    public int getCount()
    {
        return getItemCount();
    }

    @Override
    public DataType getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public void notifyDataSetChanged()
    {
        this.list.clear();
        this.list.addAll(outer_list);
        super.notifyDataSetChanged();
    }

    /**
     * View 的创建
     *
     * @param parent   父控件
     * @param viewType 类型
     * @return
     */
    public abstract viewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * ViewHolder与数据的绑定
     *
     * @param holder   viewHoler对象
     * @param data     数据
     * @param position 定位
     */
    public abstract void onBindViewHolder(viewHolder holder, DataType data, int position);

    public int getItemCount()
    {
        if (list == null)
        {
            return 0;
        }
        return list.size();
    }

    public int getItemViewType(DataType data, int position)
    {
        return super.getItemViewType(position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        viewHolder holder = null;
        DataType data = list.get(position);
        if (convertView == null)
        {
            holder = onCreateViewHolder(parent, getItemViewType(data, position));
            convertView = holder.getRootView();
        } else
        {
            holder = (viewHolder) convertView.getTag();
        }
        onBindViewHolder(holder, data, position);
        return convertView;
    }

    public static class Holder
    {
        private View root;
        private SparseArray<View> store = new SparseArray<View>();

        @SuppressWarnings("unchecked")
        public <T extends View> T get(int id)
        {
            View result = store.get(id);
            if (result == null)
            {
                result = root.findViewById(id);
                store.append(id, result);
            }
            return (T) result;
        }

        public Holder(View view)
        {
            this.root = view;
        }

        public View getRootView()
        {
            root.setTag(this);
            return root;
        }
    }
}