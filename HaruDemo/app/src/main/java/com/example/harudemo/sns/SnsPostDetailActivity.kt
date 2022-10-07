package com.example.harudemo.sns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.harudemo.R
import kotlinx.android.synthetic.main.activity_sns_post_detail.*

class SnsPostDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sns_post_detail)


        val intent = getIntent();
        val snsPostWriter = intent.getStringExtra("sns_post_writer")
        val snsPostContent = intent.getStringExtra("sns_post_content")

        sns_post_detail_writer.text = snsPostWriter
        sns_post_detail_body.text = snsPostContent


        sns_post_detail_cancel_button.setOnClickListener{
            finish()
        }

    }
}