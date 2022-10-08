package com.example.harudemo.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import kotlinx.android.synthetic.main.list_item_day.view.*
import kotlinx.android.synthetic.main.my_dialog.contv
import java.util.*

class AdapterDay(val tempMonth:Int, val dayList: MutableList<Date>, val contentlist: MutableList<String>): RecyclerView.Adapter<AdapterDay.DayView>() {
    val ROW = 6

    inner class DayView(val layout: View): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_day, parent, false)
        return DayView(view)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        holder.layout.item_day_layout.setOnClickListener {
            //요일을 클릭할 경우 dialog를 켜준다 데이터는 그 날짜
            val dlg = MyDialog(holder.layout.context)
            dlg.start("${dayList[position].year+1900}년 ${dayList[position].month+1}월 ${dayList[position].date}일\n\n\n"+holder.layout.item_day_content.text)
            //Toast.makeText(holder.layout.context, "${dayList[position]}", Toast.LENGTH_SHORT).show()
        }

        //각 날짜의 요일과 데이터 텍스트뷰에 표시
        holder.layout.item_day_text.text = dayList[position].date.toString()
        holder.layout.item_day_content.text = contentlist[position]

        //일요일 월요일 색깔 바꿈
        holder.layout.item_day_text.setTextColor(when(position % 7) {
            0 -> Color.RED
            6 -> Color.BLUE
            else -> Color.BLACK
        })

        holder.layout.item_day_content.setTextColor(Color.GRAY)

        //그 달이 아닐 경우 투명도 조절
        if(tempMonth != dayList[position].month) {
            holder.layout.item_day_text.alpha = 0.4f
            holder.layout.item_day_content.alpha = 0.4f
        }
    }

    override fun getItemCount(): Int {
        return ROW * 7
    }
}