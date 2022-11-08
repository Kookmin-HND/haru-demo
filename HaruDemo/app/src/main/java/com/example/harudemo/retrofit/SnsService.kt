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

data class SnsCommentPostRequestBodyParams(
    @SerializedName("postId")
    val postId : Int,
    @SerializedName("content")
    val content: String,
    @SerializedName("parentCommentId")
    val parentCommentId : Int
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



    @POST("${API.COMMENTS}/{email}")
    fun postComment(
        @Path("email") writer: String,
        @Body requestBodyParams: SnsCommentPostRequestBodyParams
    ): Call<JsonElement>


    @GET("${API.COMMENTS}/{postId}")
    fun getComments(
        @Path("postId") postId: Int,
    ): Call<JsonElement>

}