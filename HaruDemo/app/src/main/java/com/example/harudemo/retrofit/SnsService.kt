package com.example.harudemo.retrofit

import com.example.harudemo.utils.API
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*


data class SnsPostRequestBodyParams(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String
)



interface SnsService {
    //url:    http://10.0.2.2/api/posts/recent/{postId}
    @GET(API.RECENT_POSTS)
    fun getPosts(
        @Path("postId") id:Int
    ) : Call<JsonElement>


    @POST("${API.POSTS}/{email}")
    fun postPost(
        @Path("email") writer: String,
        @Body requestBodyParams: SnsPostRequestBodyParams
    ): Call<JsonElement>
}