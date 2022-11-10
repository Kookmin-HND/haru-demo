package com.example.harudemo.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.MainActivity
import com.example.harudemo.R
import com.example.harudemo.retrofit.AuthRetrofitManager
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.RESPONSE_STATUS
import kotlinx.android.synthetic.main.login_layout.*


//로그인 액티비티
class LoginActivity : AppCompatActivity() {

    val TAG = "LoginActivity : "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)


        // 회원가입 버튼 클릭 기능
        signUpBtn.setOnClickListener{
            Log.d(TAG, "sign up btn click")
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 클릭
        loginBtn.setOnClickListener{
            val email = userEmailEdit.text.toString()  // 변수에 입력한 e-mail 저장
            val password = userPwEdit.text.toString()        // 변수에 입력한 pw 저장

            AuthRetrofitManager.instance.loginUser(email, password, completion = { responseStatus, jsonElement ->
                when(responseStatus){
                    RESPONSE_STATUS.OKAY -> {
                        Log.d(TAG, "success Login")
                        Log.d(TAG, "${jsonElement}")
                        CustomToast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    RESPONSE_STATUS.FAIL ->{
                        Log.d(TAG, "${responseStatus} called")
                        Log.d(TAG, "${jsonElement}")
                        CustomToast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                    RESPONSE_STATUS.NO_CONTENT -> {
                        Log.d(TAG, "${responseStatus} called")
                        Log.d(TAG, "${jsonElement}")
                        CustomToast.makeText(this, "NO_CONTENT", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

    }
}