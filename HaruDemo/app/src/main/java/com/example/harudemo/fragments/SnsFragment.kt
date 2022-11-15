package com.example.harudemo.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.example.harudemo.databinding.FragmentSnsBinding
import com.example.harudemo.model.SnsPost
import com.example.harudemo.retrofit.RetrofitManager
import com.example.harudemo.retrofit.SnsRetrofitManager
import com.example.harudemo.sns.SnsAddPostActivity
import com.example.harudemo.sns.SnsDirectMessageActivity
import com.example.harudemo.sns.SnsFriendsActivity
import com.example.harudemo.sns.recyclerview.SnsPostRecyclerViewAdapter
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.RESPONSE_STATUS
import kotlinx.android.synthetic.main.fragment_sns.*
import kotlinx.android.synthetic.main.sns_post_recyclerview_footer.*

class SnsFragment : Fragment() {
    private var mBinding: FragmentSnsBinding? = null
    private val binding get() = mBinding!!

    //API에 post 데이터를 요청하기 위한 기준 id
    private var lastPostId = Int.MAX_VALUE

    // 게시물 데이터
    private var snsPostList = ArrayList<SnsPost>()

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "HomeFragment - onCreateView() called")

        //뷰바인딩
        mBinding = FragmentSnsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.setSupportActionBar(sns_top_app_bar)

        return binding.root
    }

    //레이아웃 연결 후
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //최초로 API 호출하여 데이터 얻어오기
        binding.snsPostRecyclerView.adapter = SnsPostRecyclerViewAdapter()
        infiniteScrollPostApiCall()

        binding.snsPostRecyclerView.layoutManager =
            GridLayoutManager(this.context, 1, GridLayoutManager.VERTICAL, false)

        //스크롤 이벤트 처리 함수
        initScrollListener((sns_post_recycler_view.adapter as SnsPostRecyclerViewAdapter))

        binding.btnUp.setOnClickListener {
            // 스크롤 위로 보내기
            postScrolltoTop()
        }


        // SNS 툴바 메뉴 클릭시 해당 메뉴 액티비티로 이동
        binding.snsTopAppBar.setOnMenuItemClickListener {
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

        (binding.snsPostRecyclerView.adapter as SnsPostRecyclerViewAdapter).submitList(this.snsPostList)

        //새로고침 리스너
        binding.snsSwipeRefresh.setOnRefreshListener {
            refreshPostApiCall()
        }

        binding.snsSwipeRefresh.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.BLUE);
    }

    //스크롤 이벤트 처리 함수
    private fun initScrollListener(snsPostRecyclerViewAdapter: SnsPostRecyclerViewAdapter) {
        sns_post_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 스크롤이 끝에 도달했는지 확인하고 API 호출하여 새로운 데이터 추가
                if (!binding.snsPostRecyclerView.canScrollVertically(1)) {
                    infiniteScrollPostApiCall()
                }
            }
        })
    }

    // 게시물 추가 로딩을 위한 API 호출
    private fun infiniteScrollPostApiCall() {
        if (lastPostId == 1) {
            sns_post_recycler_view_progress_bar.visibility = View.GONE
            CustomToast.makeText(App.instance, "더이상 게시물이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        SnsRetrofitManager.instance.getPosts(
            lastPostId,
            completion = { responseStatus, responseDataArrayList ->
                Log.d(TAG, "SnsFragment - ApiCallTest() called ${responseStatus}")
                when (responseStatus) {
                    //API 호출 성공
                    RESPONSE_STATUS.OKAY -> {
                        responseDataArrayList!!.forEach {
                            //마지막에 보인 게시물의 번호 초기화
                            lastPostId = it.id
                            this.snsPostList.add(it)
                        }
                        binding.snsPostRecyclerView.adapter?.notifyItemInserted(this.snsPostList.size)
                    }
                    RESPONSE_STATUS.FAIL -> {
                        CustomToast.makeText(App.instance, "api 호출 에러입니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    RESPONSE_STATUS.NO_CONTENT -> {
                        CustomToast.makeText(App.instance, "더이상 게시물이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
    }


    // 새로고침을 위한 API 호출
    private fun refreshPostApiCall() {
        lastPostId = Int.MAX_VALUE

        SnsRetrofitManager.instance.getPosts(
            lastPostId,
            completion = { responseStatus, responseDataArrayList ->
                when (responseStatus) {
                    //API 호출 성공
                    RESPONSE_STATUS.OKAY -> {
                        this.snsPostList.clear()
                        responseDataArrayList!!.forEach {
                            //마지막에 보인 게시물의 번호 초기화
                            lastPostId = it.id
                            this.snsPostList.add(it)
                        }
                        binding.snsPostRecyclerView.adapter?.notifyDataSetChanged()
                        binding.snsSwipeRefresh.isRefreshing = false
                    }
                    RESPONSE_STATUS.FAIL -> {
                        CustomToast.makeText(App.instance, "api 호출 에러입니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    RESPONSE_STATUS.NO_CONTENT -> {
                        CustomToast.makeText(App.instance, "더이상 게시물이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
    }

    //액티비티가 사라질 때 api 재호출
    override fun onResume() {
        super.onResume()
        refreshPostApiCall()
    }

    fun postScrolltoTop() {
        //이미 맨 위면 스크롤 안함
        if(binding.snsPostRecyclerView.computeVerticalScrollOffset() == 0){
            return
        }
        binding.snsPostRecyclerView.scrollToPosition(7);
        binding.snsPostRecyclerView.smoothScrollToPosition(0);
    }

}