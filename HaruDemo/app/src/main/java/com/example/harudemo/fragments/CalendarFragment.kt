package com.example.harudemo.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.harudemo.R
import com.example.harudemo.calendar.AdapterMonth
import com.example.harudemo.calendar.AdapterWeek
import com.example.harudemo.todo.TodoInputActivity
import kotlinx.android.synthetic.main.fragment_calendar.*
import okhttp3.internal.notify
import okhttp3.internal.notifyAll
import java.util.*


//상록
class CalendarFragment: Fragment() {
    companion object{
        const val TAG : String = "로그"

        fun newInstance() : CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ProfileFragmen - on Create() called" )

        val cal: Calendar = Calendar.getInstance()
        val nWeek: Int = cal.get(Calendar.DAY_OF_WEEK)

        maindata.week_content = nWeek-1

        maindata.contents[9][2] = "예비군"
        maindata.contents[9][15] = "민재형한테 돈갚기"
        maindata.contents[10][17] = "알바비 들어오는 날"
        maindata.contents[10][20] = "과제 마감"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "ProfileFragmen - onAttach() called")
    }

    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "ProfileFragmen - onCreateView() called")

        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "일정"

        calendar.layoutParams = (
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ))

        //월간 달력을 나타내게 하기 위한 레이아웃과 adapter 코드를 가져온다
        //val layoutmanager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        val monthListAdapter = AdapterMonth()

        //캘린더 즉 리사이클러뷰 레이아웃의 레이아웃 매니저와 adapter를 연결시킨 후에 scrolltoposition을 이용하여
        //터치로 넘겼을 때 다음 레이아웃의 위치를 고정시킨다.
        //calendar.layoutManager = layoutmanager

        calendar.adapter = monthListAdapter

        /*calendar.scrollToPosition(Int.MAX_VALUE/2)

        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(calendar)*/

        //주간버튼을 눌렀을 때
        weekbtn.setOnClickListener{
            if(!maindata.wom){
                //현재 상태가 주간이었는지 월간이었는지 확인 위한 변수
                maindata.wom = true

                //주간 월간버튼의 그림 바꿈
                weekbtn.setBackgroundResource(R.drawable.week_onxml)
                monthbtn.setBackgroundResource(R.drawable.month_offxml)

                //주간 달력으로 바꿈
                //val layoutmanager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                val monthListAdapter = AdapterWeek()

                val callback: OnPageChangeCallback = object : OnPageChangeCallback() {
                    override fun onPageSelected(pos: Int) {
                        super.onPageSelected(pos)
                        //println("pos:"+pos)
                        monthListAdapter.notifyDataSetChanged()
                    }
                }

                calendar.registerOnPageChangeCallback(callback)

                //calendar.layoutManager = layoutmanager
                calendar.adapter = monthListAdapter
                //calendar.scrollToPosition(Int.MAX_VALUE/2)
            }
        }

        //월간 버튼을 눌렀을 때
        monthbtn.setOnClickListener{
            if(maindata.wom){
                maindata.wom = false
                weekbtn.setBackgroundResource(R.drawable.week_offxml)
                monthbtn.setBackgroundResource(R.drawable.month_onxml)

                //월간 달력으로 바꿈
                //val layoutmanager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                val monthListAdapter = AdapterMonth()

                //calendar.layoutManager = layoutmanager
                calendar.adapter = monthListAdapter
                //calendar.scrollToPosition(Int.MAX_VALUE/2)
            }
        }

        btn_add_todo.setOnClickListener {
            val intent = Intent(context, TodoInputActivity::class.java)
            startActivity(intent)
        }
    }
}