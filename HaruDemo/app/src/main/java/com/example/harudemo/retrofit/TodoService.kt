package com.example.harudemo.retrofit

import com.example.harudemo.todo.types.Todo
import com.example.harudemo.utils.API
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class RequestBodyParams(
    @SerializedName("folder")
    val folder: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("dates")
    val dates: List<String>
)

interface TodoService {
    @GET("${API.TODOS}/{email}")
    fun getTodos(@Path("email") writer: String): Call<ArrayList<Todo>>

    @POST("${API.TODOS}/{email}")
    fun addTodos(
        @Path("email") writer: String,
        @Body requestBodyParams: RequestBodyParams
    ): Call<JsonElement>
}