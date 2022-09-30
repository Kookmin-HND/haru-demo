package com.example.harudemo.fragments

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.harudemo.R
import com.example.harudemo.sns.SnsTestActivity

class SnsFragment: Fragment() {
    companion object{
        const val TAG : String = "로그"

        fun newInstance() : SnsFragment {
            return SnsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "HomeFragment - on Create() called" )
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "HomeFragment - onAttach() called")
    }

    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "HomeFragment - onCreateView() called")

        val view = inflater.inflate(R.layout.fragment_sns, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "다른 하루"
        //버튼이 클릭되면 액비티비 호출
        val btnActivity: Button = view.findViewById(R.id.btn_newActivity)
        btnActivity.setOnClickListener{
            activity?.let{
                val intent = Intent(it, SnsTestActivity::class.java)
                it.startActivity(intent)
            }
        }
    }
}