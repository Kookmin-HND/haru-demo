package com.example.harudemo.fragments

import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentStatisticsBinding
import com.example.harudemo.grass.grassAdapter
import com.example.harudemo.sns.SnsAddPostActivity
import com.example.harudemo.sns.SnsDirectMessageActivity
import com.example.harudemo.sns.SnsFriendsActivity
import kotlinx.android.synthetic.main.fragment_sns.*
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.fragment_statistics.sns_top_app_bar
import java.util.*

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

        binding.todayBar.progress = 70 //오늘 프로그래스 바 퍼센트
        binding.todayText.text = "70%" //오늘 퍼센트 텍스트
        binding.weekBar.progress = 60 //이번주 프로그래스 바 퍼센트
        binding.weekText.text = "60%" //이번주 퍼센트 텍스트
        binding.monthBar.progress = 15 //이번달 프로그래스 바 퍼센트
        binding.monthText.text = "15%" //이번달 퍼센트 텍스트
        //프로그래스바 값 수정

        maindata.successrate[10][15] = 1
        maindata.successrate[10][19] = 1
        maindata.successrate[10][23] = 1
        maindata.successrate[10][30] = 1
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        val tempMonth = calendar.get(Calendar.MONTH)
        val dayListManager = GridLayoutManager(this.context, 7)
        val dayListAdapter = grassAdapter(tempMonth)

        statistic_vp.apply {
            layoutManager = dayListManager
            adapter = dayListAdapter

        }

        //앱바 버튼 조작
        sns_top_app_bar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sns_post_add -> {
                    activity?.let { FragmentActivity ->
                        val intent = Intent(FragmentActivity, SnsAddPostActivity::class.java)
                        FragmentActivity.startActivity(intent)
                    }
                    true
                }
                R.id.sns_friends -> {
                    activity?.let { FragmentActivity ->
                        val intent = Intent(FragmentActivity, SnsFriendsActivity::class.java)
                        FragmentActivity.startActivity(intent)
                    }
                    true
                }
                R.id.sns_direct_message -> {
                    activity?.let { FragmentActivity ->
                        val intent = Intent(FragmentActivity, SnsDirectMessageActivity::class.java)
                        FragmentActivity.startActivity(intent)
                    }
                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}