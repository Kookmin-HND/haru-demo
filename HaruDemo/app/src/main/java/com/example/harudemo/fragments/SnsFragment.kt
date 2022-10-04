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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsPost
import com.example.harudemo.sns.SnsTestActivity
import com.example.harudemo.sns.recyclerview.SnsPostRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_sns.*

class SnsFragment: Fragment() {
    private var totalCount = 0 // 전체 아이템 개수
    private var isNext = true // 다음 페이지 유무
    private var page = 0       // 현재 페이지
    private var limit = 10     // 한 번에 가져올 아이템 수

    // 데이터
    private var SnsPostList = ArrayList<SnsPost>()

    // 어답터
    private lateinit var snsPostRecyclerViewAdapter: SnsPostRecyclerViewAdapter


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
//
//
//        this.SnsPostList.add(SnsPost("LMJ", "SNS 만들기", "2022.10.04", "none"))
//        this.SnsPostList.add(SnsPost("LMJ", "SNS 만들기", "2022.10.04", "none"))
//        this.SnsPostList.add(SnsPost("LMJ", "SNS 만들기", "2022.10.04", "none"))
//        this.SnsPostList.add(SnsPost("LMJ", "SNS 만들기", "2022.10.04", "none"))
//        this.SnsPostList.add(SnsPost("LMJ", "SNS 만들기", "2022.10.04", "none"))
//
//        this.snsPostRecyclerViewAdapter = SnsPostRecyclerViewAdapter()
//        this.snsPostRecyclerViewAdapter.submitList(this.SnsPostList)
//
//
//        my_sns_recycler_view.layoutManager = GridLayoutManager(this.context, 1, GridLayoutManager.VERTICAL, false)
//        my_sns_recycler_view.adapter = this.snsPostRecyclerViewAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.SnsPostList.add(SnsPost("유상록", "내일은 학교에 가자", "2022.10.04", "none"))
        this.SnsPostList.add(SnsPost("오홍석", "로그인 만들기", "2022.10.04", "none"))
        this.SnsPostList.add(SnsPost("LMJ", "SNS 만들기", "2022.10.04", "none"))
        this.SnsPostList.add(SnsPost("LMJ", "SNS 만들기", "2022.10.04", "none"))
        this.SnsPostList.add(SnsPost("LMJ", "SNS 만들기", "2022.10.04", "none"))

        this.snsPostRecyclerViewAdapter = SnsPostRecyclerViewAdapter()
        this.snsPostRecyclerViewAdapter.submitList(this.SnsPostList)


        my_sns_recycler_view.layoutManager = GridLayoutManager(this.context, 1, GridLayoutManager.VERTICAL, false)
        my_sns_recycler_view.adapter = this.snsPostRecyclerViewAdapter



//        //버튼이 클릭되면 액비티비 호출
//        val btnActivity: Button = view.findViewById(R.id.btn_newActivity)
//        btnActivity.setOnClickListener{
//            activity?.let{
//                val intent = Intent(it, SnsTestActivity::class.java)
//                it.startActivity(intent)
//            }
//        }
    }
}