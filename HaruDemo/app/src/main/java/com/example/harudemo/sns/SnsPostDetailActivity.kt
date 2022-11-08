package com.example.harudemo.sns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.databinding.ActivitySnsPostDetailBinding
import com.example.harudemo.retrofit.SnsRetrofitManager
import com.example.harudemo.utils.RESPONSE_STATUS
import kotlinx.android.synthetic.main.activity_sns_post_detail.*
import kotlinx.android.synthetic.main.fragment_sns.*

class SnsPostDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivitySnsPostDetailBinding.inflate(layoutInflater)
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
                            Toast.makeText(App.instance, "댓글 입력에 성공했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        RESPONSE_STATUS.FAIL -> {
                            Toast.makeText(App.instance, "api 호출 에러입니다.", Toast.LENGTH_SHORT).show()
                        }
                        RESPONSE_STATUS.NO_CONTENT -> {
                            Toast.makeText(App.instance, "댓글이 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }

    }
}