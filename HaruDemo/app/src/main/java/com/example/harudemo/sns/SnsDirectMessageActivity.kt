package com.example.harudemo.sns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.harudemo.R

//SNS 프래그먼트에서 Direct Message를 보낼 수 있는 액티비티
class SnsDirectMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sns_direct_message)

    }
}