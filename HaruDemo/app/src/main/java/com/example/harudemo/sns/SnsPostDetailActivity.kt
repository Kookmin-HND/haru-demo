package com.example.harudemo.sns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.databinding.ActivitySnsAddPostBinding
import com.example.harudemo.databinding.ActivitySnsPostDetailBinding
import com.example.harudemo.databinding.FragmentSnsBinding
import com.example.harudemo.model.SnsComment
import com.example.harudemo.model.SnsPost
import com.example.harudemo.retrofit.SnsRetrofitManager
import com.example.harudemo.sns.recyclerview.SnsCommentRecyclerViewAdapter
import com.example.harudemo.sns.recyclerview.SnsPostRecyclerViewAdapter
import com.example.harudemo.utils.Constants.TAG
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.RESPONSE_STATUS
import kotlinx.android.synthetic.main.activity_sns_post_detail.*
import kotlinx.android.synthetic.main.fragment_sns.*

class SnsPostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySnsPostDetailBinding;

    // 코멘트 데이터
    private var snsCommentList = ArrayList<SnsComment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySnsPostDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.snsPostDetailCancelButton.setOnClickListener{
            finish()
        }


        val intent = getIntent();
        val snsPostId = intent.getIntExtra("sns_post_id", -1)
        val snsPostWriter = intent.getStringExtra("sns_post_writer")
        val snsPostContent = intent.getStringExtra("sns_post_content")

        sns_post_detail_writer.text = snsPostWriter
        sns_post_detail_body.text = snsPostContent

        //comment adapter 연결
        binding.snsPostCommentsRecyclerview.adapter = SnsCommentRecyclerViewAdapter()
        (binding.snsPostCommentsRecyclerview.adapter as SnsCommentRecyclerViewAdapter).submitList(this.snsCommentList)
        commentApiCall(snsPostId)

        binding.snsPostCommentsRecyclerview.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)

        sns_post_detail_cancel_button.setOnClickListener{
            finish()
        }

        //일반 댓글 입력
        binding.btnSendComment.setOnClickListener {
            if(snsPostId == -1) return@setOnClickListener

            val comment = binding.etWriteComment.text.toString()
            SnsRetrofitManager.instance.postComment(
                "LMJ",
                snsPostId,
                comment,
                -1,
                completion = { responseStatus, responseDataArrayList ->
                    when (responseStatus) {
                        //API 호출 성공
                        RESPONSE_STATUS.OKAY -> {
                            CustomToast.makeText(App.instance, "댓글 입력에 성공했습니다.", Toast.LENGTH_SHORT).show()

                            //API 재호출
                            commentApiCall(snsPostId)
                            binding.etWriteComment.setText("")
                            binding.snsPostCommentsRecyclerview.smoothScrollToPosition(100)
                        }
                        RESPONSE_STATUS.FAIL -> {
                            CustomToast.makeText(App.instance, "api 호출 에러입니다.", Toast.LENGTH_SHORT).show()
                        }
                        RESPONSE_STATUS.NO_CONTENT -> {
                            CustomToast.makeText(App.instance, "댓글이 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }

    }

    private fun commentApiCall(postId: Int){
        SnsRetrofitManager.instance.getComments(
            postId,
            completion = { responseStatus, responseDataArrayList ->
                when (responseStatus) {
                    //API 호출 성공
                    RESPONSE_STATUS.OKAY -> {
                        this.snsCommentList.clear()
                        responseDataArrayList!!.forEach {
                            this.snsCommentList.add(it)
                        }
                        binding.snsPostCommentsRecyclerview.adapter?.notifyItemInserted(this.snsCommentList.size)
                    }
                    RESPONSE_STATUS.FAIL -> {
                        CustomToast.makeText(App.instance, "api 호출 에러입니다.", Toast.LENGTH_SHORT).show()
                    }
                    RESPONSE_STATUS.NO_CONTENT -> {
                        CustomToast.makeText(App.instance, "더이상 게시물이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}