package com.example.harudemo.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.R
import com.example.harudemo.utils.CustomToast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_layout.*


//로그인 액티비티
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)


        // 회원가입 버튼 클릭 기능
        signUpBtn.setOnClickListener{
            Log.d("MainActivity : ", "sign up btn click")
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 클릭
        loginBtn.setOnClickListener{
            val email = userEmailEdit.text.toString()  // 변수에 입력한 e-mail 저장
            val pw = userPwEdit.text.toString()        // 변수에 입력한 pw 저장

            // 입력한 email, pw로 로그인 시도
            auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener{
                    task -> if(task.isSuccessful){ // firebase에 있을시 동작
                Log.d("MainActivity : ", "Login success")
                CustomToast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("MainActivity : ", "Login fail")
                CustomToast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }

            }
        }

    }
}