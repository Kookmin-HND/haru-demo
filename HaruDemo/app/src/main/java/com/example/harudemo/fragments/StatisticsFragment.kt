package com.example.harudemo.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.harudemo.R

class StatisticsFragment: Fragment() {
    companion object{
        const val TAG : String = "로그"

        fun newInstance() : StatisticsFragment {
            return StatisticsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Ranking - on Create() called" )
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

        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "기록"
    }

}