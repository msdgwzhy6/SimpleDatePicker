package com.xiaolei.simpledatepicker

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.xiaolei.datepicker.Adapter
import com.xiaolei.datepicker.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : Activity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        
        date_picker.setonDateItemClickListener { view , date ->
            val textview = view.findViewById<TextView>(R.id.tv)
            //textview.setTextColor(Color.RED)
            Toast.makeText(this , "${date.month + 1}.${date.date}" , Toast.LENGTH_SHORT).show()
        }
        
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
        
    }
}
