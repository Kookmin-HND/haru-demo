package com.example.harudemo.auth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.signup_layout.*

class SignUpActivity : AppCompatActivity(){

    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        auth = FirebaseAuth.getInstance()

        cancelBtn.setOnClickListener{
            Log.d("signActivity : ", "cancelBtn clicked")
            finish()
        }

        signUpBtn.setOnClickListener{
            Log.d("signActivity : ", "joinBtn clicked")
            val email :String = createEmailET.text.toString()
            val pw : String = createPwET.text.toString()
            val pw2 : String = createPw2ET.text.toString()
            var name : String = createNameET.text.toString()

            if (!(email.contains("@")) || (email.length < 10)) {
                Log.d("signActivity : ", "E-mail not correct")
                Toast.makeText(this, "E-Mail 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            if (pw != pw2){
                Log.d("signActivity : ", "pw, pw2 not equal")
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                        Log.d("signActivity : ", "1234")
                        Log.d("signActivity : ", "signup success")
                        Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}