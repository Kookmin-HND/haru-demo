package com.example.harudemo.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.fragments.maindata
import kotlinx.android.synthetic.main.list_item_week.view.*
import java.text.SimpleDateFormat
import java.util.*

class AdapterWeek(): RecyclerView.Adapter<AdapterWeek.WeekView>() {
    val center = Int.MAX_VALUE / 2
    //자바의 calendar 가져오기
    private var calendar = Calendar.getInstance()

    inner class WeekView(val layout: View): RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_week, parent, false)
        return WeekView(view)
    }

    fun dateDay(date : Date) : String {
        val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        val day = dayFormat.format(date)
        return day
    }

    //현재 주간의 일요일 날짜 가져오기
    fun getWeekDate() : Int{
        val mondayDate = calendar.time
        val monday = dateDay(mondayDate)

        return monday.toInt()
    }

    //날짜를 클릭할 때마다 그 요일의 일정을 가져오기 위한 함수
    fun changeadapter(holder: WeekView, tempMonth: Int, start: Int){
        //리스트의 범위를 넘지 않으면
        if (start+maindata.week_content-1 < 32) {
            println(maindata.week_content)

            //그 날짜의 데이터를 리스트로 가져오고
            var splitdata = maindata.contents[tempMonth][start + maindata.week_content - 1].split("\n")

            var contentlist: MutableList<String> = MutableList(splitdata.size) { "" }

            for (i in 0 until splitdata.size) {
                contentlist[i] = splitdata[i]
            }

            //아이템 리사이클러뷰에 집어넣어 일정을 넣어준다
            val dayListManager = LinearLayoutManager(holder.layout.context)
            val dayListAdapter = AdapterWeekItem(tempMonth, contentlist, splitdata.size)

            holder.layout.item_month_week_list.apply {
                layoutManager = dayListManager
                adapter = dayListAdapter
            }
        }
    }

    override fun onBindViewHolder(holder: WeekView, position: Int) {
        calendar.time = Date()
        calendar.set(Calendar.DAY_OF_WEEK, 1)
        calendar.add(Calendar.DATE, (position - center)*7)

        val tempMonth = calendar.get(Calendar.MONTH)
        holder.layout.week_text.text = (tempMonth+1).toString()+"월 할 일"

        var daylist = listOf(holder.layout.sundaytv, holder.layout.mondaytv, holder.layout.tuesdaytv,
                            holder.layout.wednesdaytv, holder.layout.thurstv, holder.layout.fridaytv,
                            holder.layout.saturdaytv)

        //각 요일마다 클릭하면 클릭한 위치 색깔 변환 후 그 날의 데이터 넘겨주기
        var start = getWeekDate()
        for(i in 0..6) {
            if(start+i < 31 && start+i > 0) {
                if(daylist[i].text in setOf("일","월","화","수","목","금","토","일"))
                    daylist[i].text = (start + i).toString() + "\n" + daylist[i].text

                daylist[i].setOnClickListener {
                    daylist[maindata.week_content].setBackgroundColor(Color.parseColor("#F0F0F0"))
                    daylist[i].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    maindata.week_content = i
                    changeadapter(holder, tempMonth, start)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}