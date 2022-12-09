package com.example.harudemo.calendar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.fragments.CalendarFragment
import com.example.harudemo.fragments.maindata
import com.example.harudemo.todo.TodoData
import com.example.harudemo.todo.types.Section
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.User
import kotlinx.android.synthetic.main.list_item_month.view.*
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

//월간 달력 어뎁터
class AdapterMonth: RecyclerView.Adapter<AdapterMonth.MonthView>() {
    private var calendar = Calendar.getInstance()

    inner class MonthView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val month_text: TextView = itemView.findViewById(R.id.item_month_text)
        val day_list: RecyclerView = itemView.findViewById(R.id.item_month_day_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_month,
            parent,
            false
        )
        return MonthView(view)
    }

    override fun onBindViewHolder(holder: MonthView, position: Int) {
        //캘린더를 월간 단위로 설정
        calendar.time = Date()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, position)

        holder.month_text.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
        //현재 달력이 몇 월인지 가져오고
        val tempMonth = calendar.get(Calendar.MONTH)

        var contentlist: MutableList<String> = MutableList(6*7) {""}
        var dayList: MutableList<Date> = MutableList(6 * 7) { Date() }

        var cnt = 1

        //달력의 아이템마다 값을 입력
        for(i in 0..5) {
            for(k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time

                if(calendar.time.month == tempMonth){
                    val year = calendar.get(Calendar.YEAR)
                    val content = maindata.contents[year-2022][tempMonth][cnt]

                    contentlist[i*7+k] = content

                    cnt += 1
                }
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        val dayListManager = GridLayoutManager(holder.itemView.context, 7)
        val dayListAdapter = AdapterDay(tempMonth, dayList, contentlist)

        holder.day_list.item_month_day_list.apply {
            layoutManager = dayListManager
            adapter = dayListAdapter
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}
