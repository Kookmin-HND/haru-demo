package com.example.harudemo.retrofit

import com.example.harudemo.utils.API
import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response

class AuthRetrofitManager {
    companion object{
        val instance = AuthRetrofitManager()
    }

    private val authService: AuthService? =
        RetrofitClient.getClient(API.BASE_URL)?.create(AuthService::class.java)

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
    fun loginUser(email : String, password : String, completion: (RESPONSE_STATUS, JsonElement?) -> Unit){
        val call = authService?.postLogin(LoginRequestBodyParams(email, password)) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement>{
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
}