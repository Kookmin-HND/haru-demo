package com.example.harudemo.retrofit

import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import android.util.Log
import retrofit2.Call
import retrofit2.Response

class AuthRetrofitManager {
    companion object{
        val instance = AuthRetrofitManager()
    }

    private val authService: AuthService? =
        RetrofitClient.getClient()?.create(AuthService::class.java)

    // email과 password, name을 전달해 회원가입 한다.
    fun signUpUser(email : String, password : String, name : String, completion: (RESPONSE_STATUS, JsonElement?) -> Unit){
        val call = authService?.postSignUp(SignUpRequestBodyParams(email, password, name)) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)

            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()){
                    200 -> {
                        completion(RESPONSE_STATUS.OKAY, response.body())
                    }
                }
            }
        })
    }

//     email과 password로 로그인후 토큰 반환
    fun loginUser(email : String, password : String, completion: (RESPONSE_STATUS, Pair<String, JsonElement?>) -> Unit){
        val call = authService?.postLogin(LoginRequestBodyParams(email, password)) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, Pair("", null))
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()){
                    200 -> {
                        Log.d("[debug]", "${response.body()}")
                        completion(RESPONSE_STATUS.OKAY, Pair("OKAY", response.body()))
                    }
                    400 -> {
                        Log.d("[debug]", "${response.message() is String}")
                        completion(RESPONSE_STATUS.FAIL, Pair(response.message(), null))
                    }
                    404 -> {
                        Log.d("[debug]","${response.message()}")
                        completion(RESPONSE_STATUS.FAIL, Pair(response.message(), null))
                    }
                }
            }
        })
    }

    fun loginKakao(token : String, completion: (RESPONSE_STATUS, JsonElement?) -> Unit){
        val call = authService?.postKakao(Kakaotoken(token)) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()){
                    200 -> {
                        Log.d("[debug]", "${response.body()}")
                        completion(RESPONSE_STATUS.OKAY, response.body())
                    }
//                    400 -> {
//                        Log.d("[debug]", "${response.message() is String}")
//                        completion(RESPONSE_STATUS.FAIL, Pair(response.message(), null))
//                    }
//                    404 -> {
//                        Log.d("[debug]","${response.message()}")
//                        completion(RESPONSE_STATUS.FAIL, Pair(response.message(), null))
//                    }
                }
            }
        })
    }

    // token을 기반으로 정보 검색
    fun getInfo(completion: (RESPONSE_STATUS, JsonElement?) -> Unit){
        val call = authService?.getInfo() ?:return

        call.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when(response.code()){
                    200 -> {
                        completion(RESPONSE_STATUS.OKAY, response.body())
                    }
                }
            }
        })
    }

    // token 삭제를 통한 로그아웃
    fun logoutUser(completion: (RESPONSE_STATUS, JsonElement?) -> Unit){
        val call = authService?.postLogout() ?:return

        call.enqueue(object : retrofit2.Callback<JsonElement>{
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when(response.code()){
                    200 -> {
                        completion(RESPONSE_STATUS.OKAY, response.body())
                    }
                }
            }
        })
    }
}