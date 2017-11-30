package com.xiaolei.datepicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xiaolei on 2017/11/30.
 */

public class DatePickerView extends ListView
{
    private List<WeekBean> month = new LinkedList<>();
    private DateAdapter adapter = new DateAdapter(month);
    private Date nowDate = new Date();//当前日历显示的时间
    private Adapter outAdapter = null;

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
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    private class DateAdapter extends BaseAdapter<WeekBean, BaseAdapter.Holder>
    {
        public DateAdapter(List<WeekBean> list)
        {
            super(list);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LinearLayout viewGroup = new LinearLayout(parent.getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewGroup.setLayoutParams(params);
            // ViewGroup viewGroup = (ViewGroup) View.inflate(parent.getContext(), R.layout.item_date_week, null);
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
                            break;
                        case 1:
                            outAdapter.onBindViewHolder(childHolder, data.MON.date, nowDate);
                            break;
                        case 2:
                            outAdapter.onBindViewHolder(childHolder, data.TUE.date, nowDate);
                            break;
                        case 3:
                            outAdapter.onBindViewHolder(childHolder, data.WED.date, nowDate);
                            break;
                        case 4:
                            outAdapter.onBindViewHolder(childHolder, data.THU.date, nowDate);
                            break;
                        case 5:
                            outAdapter.onBindViewHolder(childHolder, data.FRI.date, nowDate);
                            break;
                        case 6:
                            outAdapter.onBindViewHolder(childHolder, data.SAT.date, nowDate);
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

    /**
     * 根据日期取得星期几
     */
    public static String getWeek(Date date)
    {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0)
        {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 获取某天的那个月，一共有几天
     *
     * @param date
     * @return
     */
    public static int getDayCountOfMonth(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, date.getYear() + 1900);
        cal.set(Calendar.MONTH, date.getMonth());//Java月份才0开始算 
        return cal.getActualMaximum(Calendar.DATE);
    }

    /**
     * 根据输入的年月日，得到一个Date对象
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date getDate(int year, int month, int day)
    {
        Date date = new Date();
        date.setYear(year - 1900);
        date.setMonth(month - 1);
        date.setDate(day);
        return date;
    }

    /**
     * 根据输入的年月，得到一个Date对象
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getDate(int year, int month)
    {
        return getDate(year, month, 1);
    }


    public static abstract class Adapter
    {
        /**
         * 实例化UI，每个日期的UI碎片
         *
         * @param inflater
         * @param parent
         * @return
         */
        public abstract View getView(LayoutInflater inflater, ViewGroup parent);

        /**
         * 对每个UI碎片进行初始化
         *
         * @param holder       UI碎片的ViewHolder
         * @param date         当前这个UI碎片的日期
         * @param nowMonthDate 当前月份的日期
         */
        public abstract void onBindViewHolder(BaseAdapter.Holder holder, Date date, Date nowMonthDate);
    }

    public static class DefaultAdapter extends Adapter
    {
        /**
         * 实例化UI，每个日期的UI碎片
         *
         * @param inflater
         * @param parent
         * @return
         */
        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent)
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
        public void onBindViewHolder(BaseAdapter.Holder holder, Date date, Date nowMonthDate)
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
}
