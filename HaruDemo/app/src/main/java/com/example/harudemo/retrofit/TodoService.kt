package com.example.harudemo.retrofit

import com.example.harudemo.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET

interface TodoService {
    @GET(API.TODOS)
    fun getTodos(writer: String): Call<JsonElement>
}