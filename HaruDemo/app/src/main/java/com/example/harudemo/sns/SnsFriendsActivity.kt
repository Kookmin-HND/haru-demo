package com.example.harudemo.sns

import android.os.Bundle
import android.util.Log
import android.widget.ListAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.harudemo.R
import com.example.harudemo.databinding.ActivityMainBinding
import com.example.harudemo.databinding.ActivitySnsFriendsBinding
import com.example.harudemo.model.SnsPost
import com.example.harudemo.sns.recyclerview.SnsFriendsRecyclerViewAdapter
import kotlin.math.log


//SNS 프래그먼트에서 친구목록을 확인할 수 있는 액티비티
class SnsFriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnsFriendsBinding
    private lateinit var adapter: SnsFriendsRecyclerViewAdapter //adapter객체

    val mDatas = ArrayList<SnsPost>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnsFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializelist()
        initRecyclerView()

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

    fun initRecyclerView(){
        Log.d("1", "k")
        val adapter = SnsFriendsRecyclerViewAdapter()
        Log.d("1", "kk")
        adapter.snsFriendsList = mDatas
        Log.d("1", "kkk")
        binding.friendsView.adapter = adapter
        Log.d("1", "kkkk")
        binding.friendsView.layoutManager=LinearLayoutManager(this)
        Log.d("1", "kkkkk")
    }

    fun initializelist(){ //임의로 데이터 넣어서 만들어봄
        with(mDatas){
            add(SnsPost(1,"Lee min jae",null,null,null,"@drawable/ic_baseline_account_circle_24",98))
            add(SnsPost(2,"Mara sanrok","","","","@drawable/ic_baseline_account_circle_24",90))
            add(SnsPost(3,"invincible cjm","","","","@drawable/ic_baseline_account_circle_24",10))
        }
    }

}