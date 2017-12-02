[**简书博客**](http://www.jianshu.com/p/1984843623c9)

>最近遇到要做一个日历控件，给的效果图是这样的：
![日历](http://upload-images.jianshu.io/upload_images/1014308-c22666e1e7b14ff3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
其实我在想，我下次如果又要写一个，只是其中的图标改掉了，那我不得又得写一遍？？不存在的，tan90。
![生无可恋](http://upload-images.jianshu.io/upload_images/1014308-b6cc5c60783e1796.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
------------------------------------
>不知道大家有没有发现，其实所有日历都大同小异，不同的就是每个日期上的图标，文字大小，颜色，反正就是每个日期的样式不对，就好像我上面图片上框出来的那些。
既然知道，那么我们为何不把一些通用的封装起来，把不同的抽取出来，作为下次调用呢？？说干就干![呵呵](http://upload-images.jianshu.io/upload_images/1014308-58776284e5ad8889.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##思路篇：
--------------------------------------
>我这里是基于ListView写的，既然是日历控件，那么每个条数是需要通过计算得出来的，在我们开始写之前，我先贴一些工具代码出来，方便使用：

**获取某天的那个月，一共有几天**
```java
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
```

**根据输入的年月日，得到一个Date对象**
```java
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
```
**根据日期取得星期几**
```java
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
```
有了这三个工具代码，其实我们就解决了好多问题了。

------------------------------------

>因为是基于ListView改造，所以我们这个ListView肯定不可以滚动，所以我们需要加上这段代码，让ListView自适应我们的Item总和的高度：
```java
public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
{
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
}
```
还有，我们必须禁用外部setAdapter，然后设置我们自己定义的Adapter：
```java
@Override
public void setAdapter(ListAdapter adapter)
{   //禁止外部设置adapter
     // super.setAdapter(adapter);
}
public void setAdapter(Adapter adapter)
{
     outAdapter = adapter;
 }
```
-------------------------------
>当我们在设置日期的时候，要动态计算出日期的总条数，每条7天，刚好一个礼拜。
```java
/**
     * 根据传入的Date显示对应的时间日历
     *
     * @param date
     */
    public void setDate(Date date)
    {
        month.clear();
        nowDate = new Date(date.getTime());//这里做一个备份，以备下次比较使用
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
```
-----------------------------------
>我们需要自己自定义一个Adapter，因为每个View的实例化与数据的整合需要调用者自己去实现。所以我们这个Adapter这样定义：
```java
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
         * 对每个UI碎片与数据整合，以及根据不同的业务需求做不同的处理方式
         *
         * @param holder       UI碎片的ViewHolder
         * @param date         当前这个UI碎片的日期
         * @param nowMonthDate 当前月份的日期
         */
        public abstract void onBindViewHolder(BaseAdapter.Holder holder, Date date, Date nowMonthDate);
}
```
>当然，我们在这里也有一个自己的实现，在里面内置了一个**DefaultAdapter**：
```java
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
```
-------------------------
>好了，废话不多说，给你们上个图，这只是默认的样式，只需要修改外部的item的view，配合onBindViewHolder这个方法，可以达到很多变得显示效果：
![上图](http://upload-images.jianshu.io/upload_images/1014308-abfceeea69e3e185.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---------------------

##怎么使用：
>有人说，这个太难看，我想在文字右上角加一个红点点
>
>OK,没问题，我们首先自己定义一个日期的Item；
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#ffffff"
    android:gravity="center_horizontal"
    android:orientation="horizontal">

    <TextView
        android:id="@+id/tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:paddingBottom="15dp"
        android:paddingTop="15dp"
        android:text="5"
        android:textColor="#353535"
        android:textSize="15sp" />

    <View
        android:id="@+id/point"
        android:layout_width="15dp"
        android:layout_height="15dp"
        android:layout_marginTop="10dp"
        android:background="@drawable/red_point" />
</LinearLayout>
```
![效果图](http://upload-images.jianshu.io/upload_images/1014308-04b8103739e453f5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

然后，我们自己设置一个Adapter：
```java
      date_picker.setAdapter(object : Adapter()
        {
            override fun getView(inflater: LayoutInflater , parent: ViewGroup): View
            {
                //这里使用到我们自定义的item
                val view = inflater.inflate(R.layout.item_layout_date1 , parent , false)
                return view
            }

            override fun onBindViewHolder(holder: BaseAdapter.Holder , date: Date , nowMonthDate: Date)
            {
                val textview = holder.get<TextView>(R.id.tv)
                val point = holder.get<View>(R.id.point)
                // 为item上的TextView设置文字为当月的几号
                textview.text = "${date.date}"
            }
        })
```
我们来看一下效果![加红点](http://upload-images.jianshu.io/upload_images/1014308-459b38f4478d7207.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>有人又说，我想当月双数天显示红点，不是当月的，或者是当月但是是单数天的不显示，当月的文字是黑色，不是当月的文字是灰色、
>
>OK，没问题。我们这里来改造一下:
```java
        date_picker.setAdapter(object : Adapter()
        {
            override fun getView(inflater: LayoutInflater , parent: ViewGroup): View
            {
                val view = inflater.inflate(R.layout.item_layout_date1 , parent , false)
                return view
            }

            override fun onBindViewHolder(holder: BaseAdapter.Holder , date: Date , nowMonthDate: Date)
            {
                val textview = holder.get<TextView>(R.id.tv)
                val point = holder.get<View>(R.id.point)
                textview.text = "${date.date}"

                if (date.month == nowMonthDate.month)
                {
                    textview.setTextColor(Color.parseColor("#353535"))
                    if ((date.date + 1) % 2 != 0)
                    {
                        point.visibility = View.VISIBLE
                    } else
                    {
                        point.visibility = View.GONE
                    }
                } else
                {
                    textview.setTextColor(Color.parseColor("#999999"))
                    point.visibility = View.GONE
                }
            }
        })
```
显示效果：
![效果图](http://upload-images.jianshu.io/upload_images/1014308-1bbd19900ea98938.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>有人又说了，我想获取每个Item的点击事件，以便我后来处理
>
>OK，没问题
```java
        date_picker.setonDateItemClickListener { view , date ->
            Toast.makeText(this , "${date.month + 1}.${date.date}" , Toast.LENGTH_SHORT).show()
        }
```
![点击事件](http://upload-images.jianshu.io/upload_images/1014308-b7e0988469d1fb0f.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>有人可能会问，你这个控件，怎么切换日期啊？？还有我怎么知道当前日期？
>
>OK，我们首先在界面上加两个按钮，上一个月，下一个月：
>然后如下代码则可以完成你的需求：
```java
pro_mon.setOnClickListener {
    date_picker.date = date_picker.date.apply {
        month -= 1
    }
}
next_mon.setOnClickListener {
    date_picker.date = date_picker.date.apply {
        month += 1
    }
}
```
效果图：
![效果图](http://upload-images.jianshu.io/upload_images/1014308-820a826d2fb99485.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


----------------------------
>写的好匆忙，写的乱七八糟的，下次要整理一下，多放一些其他效果的示例图。
[**GitHub地址：https://github.com/xiaolei123/SimpleDatePicker**](https://github.com/xiaolei123/SimpleDatePicker)




