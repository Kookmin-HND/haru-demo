package com.example.harudemo.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.R
import com.example.harudemo.retrofit.AuthRetrofitManager
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.signup_layout.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(){

    val TAG = "SignActivity : "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        // 취소버튼 클릭시 동작
        cancelBtn.setOnClickListener{
            Log.d(TAG, "cancelBtn clicked")
            finish()
        }

        // 회원가입 버튼 클릭시 동작
        signUpBtn.setOnClickListener{
            Log.d(TAG, "joinBtn clicked")
            val email :String = createEmailET.text.toString()  // 변수에 e-mail 저장
            val pw : String = createPwET.text.toString()        // 변수에 password 저장
            val pw2 : String = createPw2ET.text.toString()      // 변수에 재확인용 password 저장
            var name : String = createNameET.text.toString()    // 변수에 사용자 이름 저장

            // email에 @없거나 길이가 10 미만이면 Toast message 출력
            if (email.contains("{") || email.contains("}")
                || email.contains("\"") || email.contains(",")
                || !(Patterns.EMAIL_ADDRESS.matcher(email).matches()) ) {
                Log.d("signActivity : ", "E-mail not correct")
                CustomToast.makeText(this, "E-Mail 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            // password와 확인용 password 다를시 Toast message 출력
            else if (pw != pw2){
                Log.d(TAG, "pw, pw2 not equal")
                CustomToast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            else if (!Pattern.matches("^[a-zA-Z0-9@!%*#?&]+\$", pw)){
                Log.d(TAG, "password의 형식을 지켜주세요")
                CustomToast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            else if (!Pattern.matches("^[a-zA-Z가-힣 ]+$", name)){
                Log.d(TAG, "wrong name")
                CustomToast.makeText(this, "이름에는 한글과 영문만 가능합니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                AuthRetrofitManager.instance.signUpUser(email, pw, name, completion =  { responseStatus, jsonElement ->
                    when (responseStatus){
                        RESPONSE_STATUS.OKAY -> {
                            Log.d(TAG, "회원가입에 성공하였습니다.")
                            CustomToast.makeText(this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                        }
                        RESPONSE_STATUS.FAIL -> {
                            Log.d(TAG, "${responseStatus} called")
                            CustomToast.makeText(this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        }
                        RESPONSE_STATUS.NO_CONTENT ->{
                            Log.d(TAG, "${responseStatus} called")
                            CustomToast.makeText(this, "NO_CONTENT", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }
}