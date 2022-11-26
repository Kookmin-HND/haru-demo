package com.example.harudemo.fragments

import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.harudemo.databinding.FragmentStatisticsBinding
import com.example.harudemo.grass.grassAdapter
import kotlinx.android.synthetic.main.fragment_sns.*
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.util.*
import kotlin.math.abs

class StatisticsFragment : Fragment() {
    companion object {
        const val TAG: String = "로그"

        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Ranking - on Create() called")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "Ranking - onAttach() called")
    }

    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "Ranking - onCreateView() called")

        //val view = inflater.inflate(R.layout.fragment_statistics, container, false)
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.setSupportActionBar(sns_top_app_bar)
        val view = binding.root

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "기록"

        // 오늘 날짜 진행도 구하기
        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        var successrate = maindata.successrate[year - 2022][month][day]
        var count = 0

        if(maindata.contents[year - 2022][month][day] != "") {
            count = maindata.contents[year - 2022][month][day].split("\n").size - 1
        }

        var rate = 0

        if(count > 0){
            rate = (successrate.toDouble()/count.toDouble()*100.0).toInt()
        }

        binding.todayBar.progress = rate //오늘 프로그래스 바 퍼센트
        binding.todayText.text = rate.toString()+"%" //오늘 퍼센트 텍스트


        //이번 주 진행도 구하기
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        year = calendar.get(Calendar.YEAR)

        successrate = 0
        count = 0

        for (i in 0..6) {
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            if (maindata.contents[year - 2022][month][day] != "") {
                count += maindata.contents[year - 2022][month][day].split("\n").size - 1
                successrate += maindata.successrate[year - 2022][month][day]
            }

            calendar.add(Calendar.DATE,1)
        }

        rate = 0

        if(count > 0){
            rate = (successrate.toDouble()/count.toDouble()*100.0).toInt()
        }
        binding.weekBar.progress = rate //이번주 프로그래스 바 퍼센트
        binding.weekText.text = rate.toString()+"%" //이번주 퍼센트 텍스트


        //이번 달 진행도 구하기
        calendar.time = Date()
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val tempmonth = calendar.get(Calendar.MONTH)

        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        successrate = 0
        count = 0

        while (calendar.get(Calendar.MONTH) == tempmonth) {
            if (maindata.contents[year - 2022][month][day] != "") {
                count += maindata.contents[year - 2022][month][day].split("\n").size - 1
                successrate += maindata.successrate[year - 2022][month][day]
            }

            day += 1
            calendar.add(Calendar.DATE, 1)
        }

        rate = 0

        if(count > 0){
            rate = (successrate.toDouble()/count.toDouble()*100.0).toInt()
        }
        binding.monthBar.progress = rate //이번달 프로그래스 바 퍼센트
        binding.monthText.text = rate.toString()+"%" //이번달 퍼센트 텍스트
        //프로그래스바 값 수정

        val dayListManager = GridLayoutManager(this.context, 7)
        val dayListAdapter = grassAdapter()

        statistic_vp.apply {
            layoutManager = dayListManager
            adapter = dayListAdapter

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}