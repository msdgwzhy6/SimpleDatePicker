package com.xiaolei.datepicker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.xiaolei.datepicker.DateUtil.*;

/**
 * Created by xiaolei on 2017/11/30.
 */

public class DatePickerView extends ListView
{
    private List<WeekBean> month = new LinkedList<>();
    private DateAdapter adapter = new DateAdapter(month);
    private Date nowDate = new Date();//当前日历显示的时间
    private Adapter outAdapter = null;
    private OnDateItemClickListener listener;

    public DatePickerView(Context context)
    {
        this(context, null);
    }

    public DatePickerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        super.setAdapter(adapter);
        setAdapter(new DefaultAdapter());
        Date date = new Date();
        setDate(date);
    }

    /**
     * 根据传入的Date显示对应的时间日历
     *
     * @param date
     */
    public void setDate(Date date)
    {
        month.clear();
        nowDate = new Date(date.getTime());//这里做一个备份，以备下次使用
        int dayCount = getItemDayCount(date);//获取当月总共几天
        int itemCount = (dayCount % 7 == 0) ? (dayCount / 7) : (dayCount / 7) + 1;
        date.setDate(1);//设置日历时间，到当月1号。
        for (int a = 0; a < itemCount; a++)//动态添加总共有几个横条
        {
            month.add(new WeekBean(new Date(date.getTime())));
            date.setDate(date.getDate() + 7);//加七天
        }
        adapter.notifyDataSetChanged();
    }

    public Date getDate()
    {
        return new Date(nowDate.getTime());
    }

    /**
     * 根据传入的时间，计算出当月第一天前几个空白+当月天数+当月后几个空白
     *
     * @param date
     * @return
     */
    private int getItemDayCount(Date date)
    {
        int dayCount = 0;
        // 1.获取当月天数，
        int days = getDayCountOfMonth(date);
        dayCount += days;
        // 2.获取当月一号星期几,可以得出前几天有几个空白
        date.setDate(1);
        String firstWeekName = getWeek(date);
        switch (firstWeekName)
        {
            case "星期日":
                dayCount += 0;
                break;
            case "星期一":
                dayCount += 1;
                break;
            case "星期二":
                dayCount += 2;
                break;
            case "星期三":
                dayCount += 3;
                break;
            case "星期四":
                dayCount += 4;
                break;
            case "星期五":
                dayCount += 5;
                break;
            case "星期六":
                dayCount += 6;
                break;
        }
        // 3.获取当月最后一天星期几，可以得出后几天有几个空白
        date.setDate(days);
        String endWeekName = getWeek(date);
        switch (endWeekName)
        {
            case "星期日":
                dayCount += 6;
                break;
            case "星期一":
                dayCount += 5;
                break;
            case "星期二":
                dayCount += 4;
                break;
            case "星期三":
                dayCount += 3;
                break;
            case "星期四":
                dayCount += 2;
                break;
            case "星期五":
                dayCount += 1;
                break;
            case "星期六":
                dayCount += 0;
                break;
        }
        return dayCount;
    }

    @Override
    public void setAdapter(ListAdapter adapter)
    {   //禁止外部设置adapter
        // super.setAdapter(adapter);
    }

    public void setAdapter(Adapter adapter)
    {
        outAdapter = adapter;
        setDate(nowDate);
    }

    /**
     * 屏蔽系统默认的ItemClickListener
     * @param listener
     */
    @Override
    public void setOnItemClickListener(@Nullable OnItemClickListener listener)
    {
        // super.setOnItemClickListener(listener);
    }

    /**
     * 设置每个时间点击事件
     * @param listener
     */
    public void setonDateItemClickListener(OnDateItemClickListener listener)
    {
        this.listener = listener;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    private class DateAdapter extends BaseAdapter<WeekBean, BaseAdapter.Holder>
    {
        private OnClickListener itemClickListener = new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Date date = (Date) view.getTag(R.id.item_data);
                if(listener != null)
                {
                    listener.onClick(view,date);
                }
            }
        };
        public DateAdapter(List<WeekBean> list)
        {
            super(list);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LinearLayout viewGroup = new LinearLayout(parent.getContext());
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewGroup.setLayoutParams(params);
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            if (outAdapter != null)
            {
                for (int a = 0; a < 7; a++)
                {
                    View childView = outAdapter.getView(inflater, viewGroup);
                    try
                    {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childView.getLayoutParams();
                        layoutParams.weight = 0;
                        layoutParams.weight = 1;
                    }catch (Exception e)
                    {
                        throw new RuntimeException("getView中，请这么写：View view = inflater.inflate(R.layout.item_date_tv, parent, false)");
                    }
                    if(!childView.hasOnClickListeners()) // 如果没有设置点击事件
                    {
                        childView.setOnClickListener(itemClickListener);
                    }
                    viewGroup.addView(childView);
                }
            } else
            {
                throw new RuntimeException("请先设置Adapter");
            }
            return new Holder(viewGroup);
        }

        @Override
        public void onBindViewHolder(Holder holder, WeekBean data, int position)
        {
            if (outAdapter != null)
            {
                ViewGroup viewGroup = (ViewGroup) holder.getRootView();
                for (int a = 0; a < 7; a++)
                {
                    View childView = viewGroup.getChildAt(a);
                    Holder childHolder = new Holder(childView);
                    switch (a)
                    {
                        case 0:
                            outAdapter.onBindViewHolder(childHolder, data.SUN.date, nowDate);
                            childView.setTag(R.id.item_data,new Date(data.SUN.date.getTime()));
                            break;
                        case 1:
                            outAdapter.onBindViewHolder(childHolder, data.MON.date, nowDate);
                            childView.setTag(R.id.item_data,new Date(data.MON.date.getTime()));
                            break;
                        case 2:
                            outAdapter.onBindViewHolder(childHolder, data.TUE.date, nowDate);
                            childView.setTag(R.id.item_data,new Date(data.TUE.date.getTime()));
                            break;
                        case 3:
                            outAdapter.onBindViewHolder(childHolder, data.WED.date, nowDate);
                            childView.setTag(R.id.item_data,new Date(data.WED.date.getTime()));
                            break;
                        case 4:
                            outAdapter.onBindViewHolder(childHolder, data.THU.date, nowDate);
                            childView.setTag(R.id.item_data,new Date(data.THU.date.getTime()));
                            break;
                        case 5:
                            outAdapter.onBindViewHolder(childHolder, data.FRI.date, nowDate);
                            childView.setTag(R.id.item_data,new Date(data.FRI.date.getTime()));
                            break;
                        case 6:
                            outAdapter.onBindViewHolder(childHolder, data.SAT.date, nowDate);
                            childView.setTag(R.id.item_data,new Date(data.SAT.date.getTime()));
                            break;
                    }
                }
            } else
            {
                throw new RuntimeException("请先设置Adapter");
            }
        }
    }

    /**
     * 当前一个礼拜的数据
     */
    private class WeekBean
    {
        public DayBean SUN;//星期天
        public DayBean MON;//星期一
        public DayBean TUE;//星期二
        public DayBean WED;//星期三
        public DayBean THU;//星期四
        public DayBean FRI;//星期五
        public DayBean SAT;//星期六

        public WeekBean(Date nowDate)
        {
            String weekName = getWeek(nowDate);
            Date startWeek = null;
            switch (weekName)
            {
                case "星期日":
                    startWeek = new Date(nowDate.getTime() - (0 * 1000 * 60 * 60 * 24));
                    break;
                case "星期一":
                    startWeek = new Date(nowDate.getTime() - (1 * 1000 * 60 * 60 * 24));
                    break;
                case "星期二":
                    startWeek = new Date(nowDate.getTime() - (2 * 1000 * 60 * 60 * 24));
                    break;
                case "星期三":
                    startWeek = new Date(nowDate.getTime() - (3 * 1000 * 60 * 60 * 24));
                    break;
                case "星期四":
                    startWeek = new Date(nowDate.getTime() - (4 * 1000 * 60 * 60 * 24));
                    break;
                case "星期五":
                    startWeek = new Date(nowDate.getTime() - (5 * 1000 * 60 * 60 * 24));
                    break;
                case "星期六":
                    startWeek = new Date(nowDate.getTime() - (6 * 1000 * 60 * 60 * 24));
                    break;
            }
            SUN = new DayBean(new Date(startWeek.getTime() + (0 * 1000 * 60 * 60 * 24)));
            MON = new DayBean(new Date(startWeek.getTime() + (1 * 1000 * 60 * 60 * 24)));
            TUE = new DayBean(new Date(startWeek.getTime() + (2 * 1000 * 60 * 60 * 24)));
            WED = new DayBean(new Date(startWeek.getTime() + (3 * 1000 * 60 * 60 * 24)));
            THU = new DayBean(new Date(startWeek.getTime() + (4 * 1000 * 60 * 60 * 24)));
            FRI = new DayBean(new Date(startWeek.getTime() + (5 * 1000 * 60 * 60 * 24)));
            SAT = new DayBean(new Date(startWeek.getTime() + (6 * 1000 * 60 * 60 * 24)));
        }
    }

    /**
     * 一天的数据
     */
    private class DayBean
    {
        public DayBean(Date date)
        {
            this.date = date;
            weekName = getWeek(date);
        }

        public Date date;//当天的日期
        public String weekName;//星期几
    }


}
