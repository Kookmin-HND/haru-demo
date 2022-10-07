package com.example.harudemo.sns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.harudemo.R

//SNS 프래그먼트에서 친구목록을 확인할 수 있는 액티비티
class SnsFriendsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sns_friends)

    }
}