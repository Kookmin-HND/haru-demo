package com.example.harudemo.sns

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.databinding.ActivitySnsAddPostBinding
import com.example.harudemo.utils.CustomToast

// SNS 프래그먼트에서 게시물을 추가할 수 있는 액티비티
class SnsAddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnsAddPostBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnsAddPostBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.addCancel.setOnClickListener {
            CustomToast.makeText(applicationContext, "취소취소", Toast.LENGTH_SHORT).show();
            finish();
        }
        binding.addApply.setOnClickListener {
            CustomToast.makeText(applicationContext, "작성작성", Toast.LENGTH_SHORT).show();
        }

        binding.addPolicy.setOnClickListener {
            CustomToast.makeText(applicationContext, "전체전체", Toast.LENGTH_SHORT).show();
        }

        binding.addimage.setOnClickListener {
            CustomToast.makeText(applicationContext, "찰칵찰칵", Toast.LENGTH_SHORT).show()
        }

    }
}