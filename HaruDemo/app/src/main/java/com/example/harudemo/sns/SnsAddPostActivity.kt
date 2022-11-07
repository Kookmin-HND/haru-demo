package com.example.harudemo.sns

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.App
import com.example.harudemo.databinding.ActivitySnsAddPostBinding
import com.example.harudemo.fragments.SnsFragment
import com.example.harudemo.retrofit.SnsRetrofitManager
import com.example.harudemo.utils.Constants.TAG
import com.example.harudemo.utils.RESPONSE_STATUS
import kotlinx.android.synthetic.main.fragment_sns.*

// SNS 프래그먼트에서 게시물을 추가할 수 있는 액티비티
class SnsAddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnsAddPostBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnsAddPostBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.addCancel.setOnClickListener {
            Toast.makeText(applicationContext, "취소취소", Toast.LENGTH_SHORT).show();
            finish();
        }


        binding.addApply.setOnClickListener {

            val title = binding.addPostTitle.text.toString()
            val content = binding.addPostText.text.toString()

            SnsRetrofitManager.instance.postPost("LMJ", title, content, completion = { responseStatus, _ ->
                when (responseStatus) {
                    //API 호출 성공
                    RESPONSE_STATUS.OKAY -> {
                        Log.d("로그", "SnsAddPostActivity - onCreate() called")
                        Toast.makeText(App.instance, "글 작성에 성공했습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    RESPONSE_STATUS.FAIL -> {
                        Log.d(TAG, "SnsAddPostActivity - onCreate() ${responseStatus} called")
                        Toast.makeText(App.instance, "글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    RESPONSE_STATUS.NO_CONTENT -> {
                        Log.d(TAG, "SnsAddPostActivity - onCreate() ${responseStatus} called")
                        Toast.makeText(App.instance, "더이상 게시물이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        binding.addPolicy.setOnClickListener {
            Toast.makeText(applicationContext, "전체전체", Toast.LENGTH_SHORT).show();
        }

        binding.addimage.setOnClickListener {
            Toast.makeText(applicationContext, "찰칵찰칵", Toast.LENGTH_SHORT).show()
        }

    }
}