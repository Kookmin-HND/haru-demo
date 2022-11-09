package com.example.harudemo.retrofit

import com.example.harudemo.utils.API
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class SignUpRequestBodyParams(
    @SerializedName("email")
    val email : String,
    @SerializedName("password")
    val password : String,
    @SerializedName("name")
    val name : String
)

data class LoginRequestBodyParams(
    @SerializedName("email")
    val email : String,
    @SerializedName("password")
    val password : String
)


interface AuthService {
    @POST("${API.USERS}/signup")
    fun postSignUp(@Body requestBodyParams: SignUpRequestBodyParams) : Call<JsonElement>

    @POST("${API.USERS}/login")
    fun postLogin(@Body requestBodyParams: LoginRequestBodyParams) : Call<JsonElement>

    @GET("${API.USERS}/info")
    fun getInfo() : Call<JsonElement>
}

