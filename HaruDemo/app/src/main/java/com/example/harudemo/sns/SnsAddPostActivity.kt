package com.example.harudemo.sns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.harudemo.R

// SNS 프래그먼트에서 게시물을 추가할 수 있는 액티비티
class SnsAddPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sns_add_post)
    }
}