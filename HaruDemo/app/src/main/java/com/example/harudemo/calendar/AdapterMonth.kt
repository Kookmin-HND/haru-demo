package com.example.harudemo.calendar

import android.text.style.TtsSpan.TextBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.fragments.maindata
import com.example.harudemo.todo.TodoData
import kotlinx.android.synthetic.main.list_item_month.view.*
import java.util.*

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

        maindata.contents = Array(30){Array(13){Array(32){""} }}

        Log.d("월",tempMonth.toString())
//        var sectiondata = TodoData.getTodos()

//        for (section in sectiondata){
//            for(todo in section.todoList){
//                var content = todo.content
//                var date = todo.date
//                var splitdate = date.split("-")
//                val year = splitdate[0].toInt()
//                val month = splitdate[1].toInt()
//                val day = splitdate[2].toInt()
//                maindata.contents[year-2022][month-1][day] += content+"\n"
//            }
//        }

        //달력의 아이템마다 값을 입력
        for(i in 0..5) {
            for(k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList[i * 7 + k] = calendar.time

                if(calendar.time.month == tempMonth){
                    val year = calendar.get(Calendar.YEAR)
                    contentlist[i*7+k] = maindata.contents[year-2022][tempMonth][cnt]
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
