package com.xiaolei.datepicker;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

/**
 * Created by xiaolei on 2017/11/30.
 */

public  abstract class Adapter
{
    /**
     * 实例化UI，每个日期的UI碎片
     *
     * @param inflater
     * @param parent
     * @return
     */
    public abstract View getView(@NonNull LayoutInflater inflater,@NonNull ViewGroup parent);

    /**
     * 对每个UI碎片进行初始化
     *
     * @param holder       UI碎片的ViewHolder
     * @param date         当前这个UI碎片的日期
     * @param nowMonthDate 当前月份的日期
     */
    public abstract void onBindViewHolder(@NonNull BaseAdapter.Holder holder, @NonNull Date date, @NonNull Date nowMonthDate);
}