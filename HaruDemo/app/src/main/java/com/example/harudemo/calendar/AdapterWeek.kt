package com.example.harudemo.calendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.fragments.maindata
import kotlinx.android.synthetic.main.list_item_week.view.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class AdapterWeek(): RecyclerView.Adapter<AdapterWeek.WeekView>() {
    //자바의 calendar 가져오기
    private var calendar = Calendar.getInstance()

    inner class WeekView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val week_text: TextView = itemView.findViewById(R.id.week_text)
        val week_list: RecyclerView = itemView.findViewById(R.id.item_month_week_list)
        val sunday: TextView = itemView.findViewById(R.id.sundaytv)
        val monthday: TextView = itemView.findViewById(R.id.mondaytv)
        val tuesday: TextView = itemView.findViewById(R.id.tuesdaytv)
        val wednesday: TextView = itemView.findViewById(R.id.wednesdaytv)
        val thursday: TextView = itemView.findViewById(R.id.thurstv)
        val friday: TextView = itemView.findViewById(R.id.fridaytv)
        val saturday: TextView = itemView.findViewById(R.id.saturdaytv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekView {
        calendar.time = Date()
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_week,
            parent,
            false
        )
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
    fun changeadapter(holder: WeekView, tempYear: Int, tempMonth: Int, start: Int){
        //리스트의 범위를 넘지 않으면
        if (start+maindata.week_content-1 < 32) {
            println(maindata.week_content)

            //그 날짜의 데이터를 리스트로 가져오고
            var splitdata = maindata.contents[tempYear-2022][tempMonth][start + maindata.week_content].split("\n")

            var contentlist: MutableList<String> = MutableList(splitdata.size) { "" }

            for (i in 0 until splitdata.size) {
                contentlist[i] = splitdata[i]
            }

            //아이템 리사이클러뷰에 집어넣어 일정을 넣어준다
            val dayListManager = LinearLayoutManager(holder.itemView.context)
            val dayListAdapter = AdapterWeekItem(tempMonth, contentlist, splitdata.size)

            holder.week_list.apply {
                layoutManager = dayListManager
                adapter = dayListAdapter
            }
        }
    }

    override fun onBindViewHolder(holder: WeekView, position: Int) {
        //println("position:"+position)
        calendar.time = Date()
        calendar.set(Calendar.DAY_OF_WEEK, 1)
        calendar.add(Calendar.DATE, position*7)

        val tempYear = calendar.get(Calendar.YEAR)
        val tempMonth = calendar.get(Calendar.MONTH)
        holder.week_text.text = (tempMonth+1).toString()+"월 할 일"

        var daylist = listOf(holder.sunday, holder.monthday, holder.tuesday,
                            holder.wednesday, holder.thursday, holder.friday,
                            holder.saturday)

        for(i in 0..6) {
            daylist[i].setBackgroundColor(Color.parseColor("#F0F0F0"))
        }

        //각 요일마다 클릭하면 클릭한 위치 색깔 변환 후 그 날의 데이터 넘겨주기
        var start = getWeekDate()
        for(i in 0..6) {
            if(start+i < 31 && start+i > 0) {
                if(daylist[i].text in setOf("일","월","화","수","목","금","토","일")) {
                    daylist[i].text = (start + i).toString() + "\n" + daylist[i].text
                }

                daylist[i].setOnClickListener {
                    daylist[maindata.week_content].setBackgroundColor(Color.parseColor("#F0F0F0"))
                    daylist[i].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    maindata.week_content = i
                    changeadapter(holder, tempYear, tempMonth, start)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}