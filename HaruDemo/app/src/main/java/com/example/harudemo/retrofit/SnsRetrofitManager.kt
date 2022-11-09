package com.example.harudemo.retrofit

import android.util.Log
import com.example.harudemo.model.SnsComment
import com.example.harudemo.model.SnsPost
import com.example.harudemo.utils.API
import com.example.harudemo.utils.Constants.TAG
import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class SnsRetrofitManager {
    companion object {
        val instance = SnsRetrofitManager()
    }

    private val snsService: SnsService? =
        RetrofitClient.getClient(API.BASE_URL)?.create(SnsService::class.java)

    //SNS에서 게시물을 호출하는 함수
    fun getPosts(
        id: Int,
        completion: (RESPONSE_STATUS, ArrayList<SnsPost>?) -> Unit
    ) {

        val call = snsService?.getPosts(id) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            //응답 실패 시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called")
                completion(RESPONSE_STATUS.FAIL, null)
            }

            //응답 성공 시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")

                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            val parsedSnsPostDataArray = ArrayList<SnsPost>()
                            val results = it.asJsonArray

                            // 데이터가 있다면
                            results.forEach { resultItem ->
                                val resultItemObject = resultItem.asJsonObject
                                val postId = resultItemObject.get("id").asInt
                                val writer = resultItemObject.get("writer").asString
                                val content =
                                    resultItemObject.get("content").asString
                                val createdAt = resultItemObject.get("createdAt").asString
                                val updatedAt = resultItemObject.get("updatedAt").asString

                                val snsPostItem = SnsPost(
                                    id = postId,
                                    writer = writer,
                                    content = content,
                                    createdAt = createdAt,
                                    updatedAt = updatedAt,
                                    writerPhoto = "",
                                )
                                parsedSnsPostDataArray.add(snsPostItem)
                            }
                            completion(RESPONSE_STATUS.OKAY, parsedSnsPostDataArray)
                        }
                    }
                }
            }
        })
    }



    //이미지 추가버전
    //SNS에서 글쓰기를 저장하는 함수
    fun postPost(
        writer: String,
        title: String,
        content: String,
        images: ArrayList<MultipartBody.Part>?,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {

        val call =
            snsService?.postPost(writer, images, SnsPostRequestBodyParams(title, content)) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            completion(RESPONSE_STATUS.OKAY, it)
                        }
                    }
                    400 -> {
                        Log.d("[debug]", response.body().toString())
                    }
                }
            }
        })
    }




    //SNS에서 댓글을 저장하는 함수
    fun postComment(
        writer: String,
        postId: Int,
        content: String,
        parentCommentId: Int,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {

        val call =
            snsService?.postComment(writer, SnsCommentPostRequestBodyParams(postId, content, parentCommentId)) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            completion(RESPONSE_STATUS.OKAY, it)
                        }
                    }
                    400 -> {
                        Log.d("[debug]", response.body().toString())
                    }
                }
            }
        })
    }


    //SNS에서 댓글을 저장하는 함수
    fun getComments(
        postId: Int,
        completion: (RESPONSE_STATUS, ArrayList<SnsComment>?) -> Unit
    ) {

        val call =
            snsService?.getComments(postId) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            val parsedSnsCommentDataArray = ArrayList<SnsComment>()
                            val results = it.asJsonArray

                            // 데이터가 있다면
                            results.forEach { resultItem ->
                                val resultItemObject = resultItem.asJsonObject
                                val writer = resultItemObject.get("writer").asString
                                val parentCommentId = resultItemObject.get("parentCommentId").asInt
                                val content = resultItemObject.get("content").asString
                                val id = resultItemObject.get("id").asInt
                                val createdAt = resultItemObject.get("createdAt").asString
                                val updatedAt = resultItemObject.get("updatedAt").asString

                                val snsCommentItem = SnsComment(
                                    id,
                                    postId,
                                    writer,
                                    parentCommentId,
                                    content,
                                    createdAt,
                                    updatedAt,
                                    writerPhoto = "",
                                )
                                parsedSnsCommentDataArray.add(snsCommentItem)
                            }
                            completion(RESPONSE_STATUS.OKAY, parsedSnsCommentDataArray)
                        }
                    }
                    400 -> {
                        Log.d("[debug]", response.body().toString())
                    }
                }
            }
        })
    }
}