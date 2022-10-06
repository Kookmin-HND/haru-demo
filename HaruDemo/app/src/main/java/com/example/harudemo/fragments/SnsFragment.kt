package com.example.harudemo.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsPost
import com.example.harudemo.sns.SnsAddPostActivity
import com.example.harudemo.sns.SnsDirectMessageActivity
import com.example.harudemo.sns.SnsFriendsActivity
import com.example.harudemo.sns.recyclerview.SnsPostRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_sns.*

class SnsFragment : Fragment() {
    private var totalCount = 0 // 전체 아이템 개수
    private var isNext = true // 다음 페이지 유무
    private var page = 0       // 현재 페이지
    private var limit = 10     // 한 번에 가져올 아이템 수

    // 데이터
    private var SnsPostList = ArrayList<SnsPost>()

    // 어답터
    private lateinit var snsPostRecyclerViewAdapter: SnsPostRecyclerViewAdapter


    companion object {
        const val TAG: String = "로그"

        fun newInstance(): SnsFragment {
            return SnsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "HomeFragment - on Create() called")
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

        (activity as AppCompatActivity?)!!.setSupportActionBar(sns_top_app_bar)
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

        my_sns_recycler_view.layoutManager =
            GridLayoutManager(this.context, 1, GridLayoutManager.VERTICAL, false)
        my_sns_recycler_view.adapter = this.snsPostRecyclerViewAdapter


        // SNS 툴바 메뉴 클릭시 해당 메뉴 액티비티로 이동
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


//        //버튼이 클릭되면 액비티비 호출
//        val btnActivity: Button = view.findViewById(R.id.btn_newActivity)
//        btnActivity.setOnClickListener{
//            activity?.let{
//                val intent = Intent(it, SnsTestActivity::class.java)
//                it.startActivity(intent)
//            }
//        }
    }


//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle item selection
//
//        Log.d(SnsFragment.TAG, "SnsFragment - onOptionsItemSelected() called")
//        return when (item.itemId) {
//            R.id.sns_post_add -> {
//                Toast.makeText(App.instance, "sns_post", Toast.LENGTH_SHORT).show()
//                true
//            }
//            R.id.sns_friends -> {
//                Toast.makeText(App.instance, "sns_friends", Toast.LENGTH_SHORT).show()
//                true
//            }
//            R.id.sns_direct_message -> {
//                Toast.makeText(App.instance, "sns_direct_message", Toast.LENGTH_SHORT).show()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


}