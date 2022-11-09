package com.example.harudemo.sns

import android.os.Bundle
import android.widget.ListAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.harudemo.R
import com.example.harudemo.databinding.ActivitySnsFriendsBinding
import com.example.harudemo.model.SnsPost


//SNS 프래그먼트에서 친구목록을 확인할 수 있는 액티비티
class SnsFriendsActivity : AppCompatActivity() {
    private val FriendsList = ArrayListOf<SnsPost>
    val listAdapter = ListAdapter(itemList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySnsFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.friendsView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.friendsView.adapter = ListAdapter(FriendsList)

        listAdapter.notifyDataSetChanged()

    }
}