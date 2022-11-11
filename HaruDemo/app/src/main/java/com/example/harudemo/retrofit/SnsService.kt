package com.example.harudemo.retrofit

import com.example.harudemo.utils.API
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    val postId: Int,
    @SerializedName("content")
    val content: String,
    @SerializedName("parentCommentId")
    val parentCommentId: Int
)


interface SnsService {
    //url:    http://10.0.2.2/api/posts/recent/{postId}
    @GET(API.RECENT_POSTS)
    fun getPosts(
        @Path("postId") id: Int
    ): Call<JsonElement>


//
//    // 이미지 추가 버전
//    @Multipart
//    @POST("${API.POSTS}/{email}")
//    fun postPost(
//        @Path("email") writer: String,
//        @Part images: List<MultipartBody.Part>?,
//        @Part("requestBodyParams") requestBodyParams: SnsPostRequestBodyParams
//    ): Call<JsonElement>


    // 이미지 추가 버전
    @Multipart
    @POST("${API.POSTS}/{email}")
    fun postPost(
        @Path("email") writer: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part images: List<MultipartBody.Part>?
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