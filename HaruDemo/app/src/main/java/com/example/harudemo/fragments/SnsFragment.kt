package com.example.harudemo.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    //레이아웃 연결 후
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ApiCallTest()
        this.snsPostRecyclerViewAdapter = SnsPostRecyclerViewAdapter()
        this.snsPostRecyclerViewAdapter.submitList(this.SnsPostList)

        sns_post_recycler_view.layoutManager =
            GridLayoutManager(this.context, 1, GridLayoutManager.VERTICAL, false)
        sns_post_recycler_view.adapter = this.snsPostRecyclerViewAdapter

        //스크롤 이벤트 처리 함수
        initScrollListener(this.snsPostRecyclerViewAdapter)

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
    }


    //스크롤 이벤트 처리 함수
    private fun initScrollListener(snsPostRecyclerViewAdapter: SnsPostRecyclerViewAdapter) {
        sns_post_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = sns_post_recycler_view.layoutManager

                //뷰 상에서 마지막으로 보인 아이템 찾기
                val lastVisibleItem = (layoutManager as LinearLayoutManager)
                    .findLastVisibleItemPosition()

                //레이아웃 매니저가 지닌 전체 아이템 갯수와 현재 뷰에서 마지막 보이는 아이템 개수를 비교하여 API Call
                if (layoutManager.itemCount <= lastVisibleItem + 2) {
                    ApiCallTest()
                    snsPostRecyclerViewAdapter.notifyItemInserted(SnsPostList.size)

                }
            }
        })
    }


    fun ApiCallTest() {
        this.SnsPostList.add(
            SnsPost(
                "유상록",
                "게시물번호 " + (totalCount++).toString(),
                "2022.10.04",
                "none"
            )
        )
        this.SnsPostList.add(
            SnsPost(
                "오홍석",
                "게시물번호 " + (totalCount++).toString(),
                "2022.10.04",
                "none"
            )
        )
        this.SnsPostList.add(
            SnsPost(
                "LMJ",
                "게시물번호 " + (totalCount++).toString(),
                "2022.10.04",
                "none"
            )
        )
        this.SnsPostList.add(
            SnsPost(
                "LMJ",
                "게시물번호 " + (totalCount++).toString(),
                "2022.10.04",
                "none"
            )
        )
        this.SnsPostList.add(
            SnsPost(
                "LMJ",
                "게시물번호 " + (totalCount++).toString(),
                "2022.10.04",
                "none"
            )
        )
    }
}