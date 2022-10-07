package com.example.harudemo.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_layout.*


//로그인 액티비티
class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        auth = FirebaseAuth.getInstance()

        signUpBtn.setOnClickListener{
            Log.d("MainActivity : ", "sign up btn click")
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener{
            val email = userEmailEdit.text.toString()
            val pw = userPwEdit.text.toString()
            auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener{
                    task -> if(task.isSuccessful){
                Log.d("MainActivity : ", "Login success")
                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("MainActivity : ", "Login fail")
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }

            }
        }

    }
}