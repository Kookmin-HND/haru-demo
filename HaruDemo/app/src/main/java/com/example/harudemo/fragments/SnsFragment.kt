package com.example.harudemo.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsPost
import com.example.harudemo.retrofit.RetrofitManager
import com.example.harudemo.sns.SnsAddPostActivity
import com.example.harudemo.sns.SnsDirectMessageActivity
import com.example.harudemo.sns.SnsFriendsActivity
import com.example.harudemo.sns.recyclerview.SnsPostRecyclerViewAdapter
import com.example.harudemo.utils.RESPONSE_STATUS
import kotlinx.android.synthetic.main.fragment_sns.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

                Log.d(TAG, "SnsFragment - onScrolled() called ${layoutManager?.itemCount}")

                //뷰 상에서 마지막으로 보인 아이템 찾기
                val lastVisibleItem = (layoutManager as LinearLayoutManager)
                    .findLastVisibleItemPosition()

                // 스크롤이 끝에 도달했는지 확인
                if (!sns_post_recycler_view.canScrollVertically(1)) {
                    ApiCallTest()
                }
                //레이아웃 매니저가 지닌 전체 아이템 갯수와 현재 뷰에서 마지막 보이는 아이템 개수를 비교하여 API Call
//                if (layoutManager.itemCount <= lastVisibleItem + 2) {
//
//                }
            }
        })
    }


    fun ApiCallTest() {
//        this.SnsPostList.add(
//            SnsPost(
//                "유상록",
//                "게시물번호 " + (totalCount++).toString(),
//                "2022.10.04",
//                "none"
//            )
//        )
//        this.SnsPostList.add(
//            SnsPost(
//                "유상록",
//                "게시물번호 " + (totalCount++).toString(),
//                "2022.10.04",
//                "none"
//            )
//        )
//        this.SnsPostList.add(
//            SnsPost(
//                "유상록",
//                "게시물번호 " + (totalCount++).toString(),
//                "2022.10.04",
//                "none"
//            )
//        )
//        this.SnsPostList.add(
//            SnsPost(
//                "유상록",
//                "게시물번호 " + (totalCount++).toString(),
//                "2022.10.04",
//                "none"
//            )
//        )
//
//        Log.d(TAG, "SnsFragment - ApiCallTest() called, 스크롤 리스너")


        RetrofitManager.instance.getPosts(completion = { responseStatus, responseDataArrayList ->
            Log.d(TAG, "SnsFragment - ApiCallTest() called ${responseStatus}")
            when (responseStatus) {
                //API 호출 성공

                RESPONSE_STATUS.OKAY -> {
                    responseDataArrayList!!.forEach {
                        this.SnsPostList.add(it)
                    }

                    snsPostRecyclerViewAdapter.notifyItemInserted(SnsPostList.size)
                }
                RESPONSE_STATUS.FAIL -> {
                    Toast.makeText(App.instance, "api 호출 에러입니다.", Toast.LENGTH_SHORT).show()
                }
                RESPONSE_STATUS.NO_CONTENT -> {
                    Toast.makeText(App.instance, "더이상 게시물이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}