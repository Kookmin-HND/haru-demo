package com.example.harudemo.retrofit

import com.example.harudemo.todo.types.Todo
import com.example.harudemo.utils.API
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*

data class PostRequestBodyParams(
    @SerializedName("folder")
    val folder: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("dates")
    val dates: List<String>
)

data class DeleteRequestBodyParams(
    @SerializedName("id")
    val id: Number
)

interface TodoService {
    @GET("${API.TODOS}/{email}")
    fun getTodos(@Path("email") writer: String): Call<ArrayList<Todo>>

    @POST("${API.TODOS}/{email}")
    fun addTodos(
        @Path("email") writer: String,
        @Body requestBodyParams: PostRequestBodyParams
    ): Call<JsonElement>

    @HTTP(method = "DELETE", path = API.TODOS, hasBody = true)
    fun deleteTodo(@Body id: DeleteRequestBodyParams): Call<JsonElement>
}