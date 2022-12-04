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
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.login_layout.*
import org.json.JSONObject


//로그인 액티비티
class LoginActivity : AppCompatActivity() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    val TAG = "[debug]"

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceUtil(applicationContext)  // preference
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)


        var currentUser = prefs.getString("currentUser")


        if (currentUser != null) {
            val json = JSONObject(currentUser)
            User.info.id = json.getInt("id")
            User.info.email = json.getString("email")
            User.info.name = json.getString("name")
            User.info.createAt = json.getString("createAt")
            User.info.token = json.getString("token")

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            User.info = UserInfo()
        }
        Log.d("[debug]", "token : ${User.info.token}")

        // 회원가입 버튼 클릭 기능
        signUpBtn.setOnClickListener {
            Log.d(TAG, "sign up btn click")
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 클릭
        loginBtn.setOnClickListener {
            val email = userEmailEdit.text.toString()  // 변수에 입력한 e-mail 저장
            val password = userPwEdit.text.toString()        // 변수에 입력한 pw 저장

            if (email == "" || password == ""){
                CustomToast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AuthRetrofitManager.instance.loginUser(
                email,
                password,
                completion = { responseStatus, pair ->
                    val errMessage = pair.first
                    val jsonElement = pair.second
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> {
                            Log.d(TAG, "success Login")

                            val json = JSONObject(jsonElement.toString())
                            User.info = UserInfo(
                                json.getInt("id"),
                                json.getString("email"),
                                json.getString("name"),
                                json.getString("createAt"),
                                json.getString("token")
                            )
                            prefs.setString("currentUser", json.toString())
                            CustomToast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        RESPONSE_STATUS.FAIL -> {
                            Log.d(TAG, "${responseStatus} called")
                            Log.d(TAG, "${errMessage}")
                            when (errMessage) {
                                "Bad Request" -> {
                                    CustomToast.makeText(
                                        this,
                                        "비밀번호를 다시 입력해주세요.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                "Not Found" -> {
                                    CustomToast.makeText(
                                        this,
                                        "존재하지 않는 이메일 입니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
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
            Log.d(TAG, "onCreate: 카카오 ${error}")
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        CustomToast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        CustomToast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        CustomToast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        CustomToast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        CustomToast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        CustomToast.makeText(
                            this,
                            "설정이 올바르지 않음(android key hash)",
                            Toast.LENGTH_SHORT
                        ).show()
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
            } else if (token != null) {
//                CustomToast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("[debug]", "${token.accessToken}")
                AuthRetrofitManager.instance.loginKakao(
                    token.accessToken,
                    completion = { responseStatus, jsonElement ->
                        when (responseStatus) {
                            RESPONSE_STATUS.FAIL -> {
                                Log.d("[debug]", "false")
                            }
                            RESPONSE_STATUS.OKAY -> {
                                Log.d("[debug]", "success")

                                val json = JSONObject(jsonElement.toString())
                                User.info = UserInfo(
                                    json.getInt("id"),
                                    json.getString("email"),
                                    json.getString("name"),
                                    json.getString("createAt"),
                                    json.getString("token")
                                )
                                prefs.setString("currentUser", json.toString())
                                CustomToast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            RESPONSE_STATUS.NO_CONTENT -> {
                                Log.d("[debug]", "no content")
                            }
                        }
                    })
//                1. 받은 토큰을 백으로 넘긴다.
//                2. 서버에서는 받은 토큰은 카카오 서버에 넘겨 인증이 유효한지 확인
//                3. 유효하면 유저 이메일하고 이름을 같이 받는다
//                4. 받은 이메일과 이름을 기반으로 DB에 업데이트 한다.
//                5. 업데이트한 유저 정보를 기반으로 JWT를 만들어 프론트로 보낸다.
//                6. 프로트에서 사용


//                UserApiClient.instance.me { user, error ->
//                    if (error != null){
//                        Log.e(TAG, "사용자 정보 요청 실패", error)
//                    }else{
//
//                    }
//                }
            }

        }


        kakaoLoginBtn.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

    }
}