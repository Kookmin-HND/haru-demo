package com.example.harudemo.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.MainActivity
import com.example.harudemo.R
import com.example.harudemo.model.UserInfo
import com.example.harudemo.retrofit.AuthRetrofitManager
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.PreferenceUtil
import com.example.harudemo.utils.RESPONSE_STATUS
import com.example.harudemo.utils.User
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.login_layout.*
import org.json.JSONObject


//로그인 액티비티
class LoginActivity : AppCompatActivity() {
    companion object{
        lateinit var prefs : PreferenceUtil
    }
    val TAG = "LoginActivity : "

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceUtil(applicationContext)  // preference
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        var currentUser = prefs.getString("currentUser")

        if (currentUser != null) {
            val json = JSONObject(currentUser)
            User.info = UserInfo(json.getInt("id"),
                json.getString("email"),
                json.getString("name"),
                json.getString("createAt"),
                json.getString("token"))
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

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

                        val json = JSONObject(jsonElement.toString())
                        User.info = UserInfo(json.getInt("id"),
                            json.getString("email"),
                            json.getString("name"),
                            json.getString("createAt"),
                            json.getString("token"))
                        prefs.setString("currentUser", json.toString())
                        CustomToast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
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

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        CustomToast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        CustomToast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        CustomToast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        CustomToast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        CustomToast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        CustomToast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        CustomToast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        CustomToast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        CustomToast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if (token != null) {
                CustomToast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }


        kakaoLoginBtn.setOnClickListener {
            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            }else{
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

    }
}