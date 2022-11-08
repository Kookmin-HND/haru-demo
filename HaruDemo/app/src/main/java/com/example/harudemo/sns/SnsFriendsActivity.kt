package com.example.harudemo.sns

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.R
import com.example.harudemo.databinding.ActivitySnsFriendsBinding
import com.example.harudemo.model.SnsPost


//SNS 프래그먼트에서 친구목록을 확인할 수 있는 액티비티
class SnsFriendsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySnsFriendsBinding;

    private val FriendsList: ArrayList<SnsPost>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sns_friends)
        binding = ActivitySnsFriendsBinding.inflate(layoutInflater)

        binding.follower.setOnClickListener{
            Toast.makeText(applicationContext, "팔로워 클릭",Toast.LENGTH_SHORT).show()
        }
        binding.following.setOnClickListener{
            Toast.makeText(applicationContext, "팔로잉 클릭",Toast.LENGTH_SHORT).show()
        }
        binding.clearText.setOnClickListener{
            binding.search.setText(null)
        }
        binding.cancel.setOnClickListener{
            finish()
        }

    }
}