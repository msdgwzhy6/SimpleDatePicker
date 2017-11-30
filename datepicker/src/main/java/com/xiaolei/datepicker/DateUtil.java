package com.xiaolei.datepicker;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具
 * Created by xiaolei on 2017/12/1.
 */

public class DateUtil
{
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
}
