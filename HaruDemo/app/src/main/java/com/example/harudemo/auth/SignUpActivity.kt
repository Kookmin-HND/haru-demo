package com.example.harudemo.auth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.signup_layout.*

class SignUpActivity : AppCompatActivity(){

    private lateinit var auth : FirebaseAuth  // FirebaseAuth 객체 생성
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        auth = FirebaseAuth.getInstance() // instance가져오기

        // 취소버튼 클릭시 동작
        cancelBtn.setOnClickListener{
            Log.d("signActivity : ", "cancelBtn clicked")
            finish()
        }

        // 회원가입 버튼 클릭시 동작
        signUpBtn.setOnClickListener{
            Log.d("signActivity : ", "joinBtn clicked")
            val email :String = createEmailET.text.toString()  // 변수에 e-mail 저장
            val pw : String = createPwET.text.toString()        // 변수에 password 저장
            val pw2 : String = createPw2ET.text.toString()      // 변수에 재확인용 password 저장
            var name : String = createNameET.text.toString()    // 변수에 사용자 이름 저장

            // email에 @없거나 길이가 10 미만이면 Toast message 출력
            if (!(email.contains("@")) || (email.length < 10)) {
                Log.d("signActivity : ", "E-mail not correct")
                Toast.makeText(this, "E-Mail 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            // password와 확인용 password 다를시 Toast message 출력
            if (pw != pw2){
                Log.d("signActivity : ", "pw, pw2 not equal")
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            else {  // 위 조건 통과시 firebase에 사용자 추가 시도
                auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(this) { task ->
                    // 정상적으로 이루어지면 Toast message 출력 후, SignUpActivity 종료
                    if (task.isSuccessful) {
                        Log.d("signActivity : ", "signup success")
                        Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}