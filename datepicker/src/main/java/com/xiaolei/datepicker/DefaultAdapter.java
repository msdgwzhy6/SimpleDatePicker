package com.xiaolei.datepicker;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by xiaolei on 2017/11/30.
 */

public class DefaultAdapter extends Adapter
{
    /**
     * 实例化UI，每个日期的UI碎片
     *
     * @param inflater
     * @param parent
     * @return
     */
    @Override
    public View getView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent)
    {
        View view = inflater.inflate(R.layout.item_date_tv, parent, false);
        return view;
    }

    /**
     * 对每个UI碎片进行初始化
     *
     * @param holder       UI碎片的ViewHolder
     * @param date         当前这个UI碎片的日期
     * @param nowMonthDate 当前月份的日期
     */
    @Override
    public void onBindViewHolder(@NonNull BaseAdapter.Holder holder, @NonNull Date date, @NonNull Date nowMonthDate)
    {
        TextView textView = holder.get(R.id.tv);
        textView.setText(date.getDate() + "");

        if (date.getMonth() == nowMonthDate.getMonth())//如果是当前月
        {
            textView.setTextColor(Color.parseColor("#353535"));
        } else
        {
            textView.setTextColor(Color.parseColor("#999999"));
        }
    }
}