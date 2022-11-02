package com.example.harudemo.retrofit

import com.example.harudemo.todo.types.Todo
import com.example.harudemo.utils.API
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TodoService {
    @GET("${API.TODOS}/{email}")
    fun getTodos(@Path("email") writer: String): Call<ArrayList<Todo>>
}