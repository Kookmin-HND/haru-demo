package com.example.harudemo.retrofit

import android.util.Log
import com.example.harudemo.model.SnsComment
import com.example.harudemo.model.SnsImage
import com.example.harudemo.model.SnsPost
import com.example.harudemo.utils.Constants.TAG
import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class SnsRetrofitManager {
    companion object {
        val instance = SnsRetrofitManager()
    }

    private val snsService: SnsService? =
        RetrofitClient.getClient()?.create(SnsService::class.java)

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
                                val category = resultItemObject.get("category").asString
                                val writer = resultItemObject.get("writer").asString
                                val content =
                                    resultItemObject.get("content").asString
                                val createdAt = resultItemObject.get("createdAt").asString
                                val updatedAt = resultItemObject.get("updatedAt").asString
                                val postImageFiles = resultItemObject.get("imageFiles").asJsonArray
                                val postLikeListJson = resultItemObject.get("likes").asJsonArray

                                // 댓글 개수 받기
                                val commentNumber =
                                    resultItemObject.get("comments").asJsonArray.size()

                                val postImageList = ArrayList<String>()
                                val postLikeList = ArrayList<String>()

                                //이미지 url을 배열에 넣기
                                postImageFiles.forEach { imageFile ->
                                    postImageList.add(imageFile.asJsonObject.get("url").asString)
                                }

                                postLikeListJson.forEach { item ->
                                    postLikeList.add(item.asJsonObject.get("user").asString)
                                }


                                val snsPostItem = SnsPost(
                                    id = postId,
                                    writer = writer,
                                    category = category,
                                    content = content,
                                    createdAt = createdAt,
                                    updatedAt = updatedAt,
                                    writerPhoto = "",
                                    average = 0,
                                    commentNumber = commentNumber,
                                    postLikeList = postLikeList,
                                    postImageList = postImageList,
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
        category: RequestBody,
        content: RequestBody,
        images: ArrayList<MultipartBody.Part>?,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {

        val call =
            snsService?.postPost(writer, category, content, images) ?: return

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
            snsService?.postComment(
                writer,
                SnsCommentPostRequestBodyParams(postId, content, parentCommentId)
            ) ?: return

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


    //SNS에서 댓글을 불러오는 함수
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
                                val commentLikeListJson = resultItemObject.get("likes").asJsonArray

                                val createdAt = resultItemObject.get("createdAt").asString
                                val updatedAt = resultItemObject.get("updatedAt").asString

                                val commentLikeList = ArrayList<String>()

                                commentLikeListJson.forEach { item ->
                                    commentLikeList.add(item.asJsonObject.get("user").asString)
                                }

                                val snsCommentItem = SnsComment(
                                    id,
                                    postId,
                                    writer,
                                    parentCommentId,
                                    content,
                                    createdAt,
                                    updatedAt,
                                    commentLikeList,
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


    //SNS에서 이미지를 불러오는 함수
    fun getImages(
        postId: Int,
        completion: (RESPONSE_STATUS, ArrayList<SnsImage>?) -> Unit
    ) {

        val call =
            snsService?.getImages(postId) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            val parsedSnsImageDataArray = ArrayList<SnsImage>()
                            val results = it.asJsonArray

                            // 데이터가 있다면
                            results.forEach { resultItem ->
                                val resultItemObject = resultItem.asJsonObject
                                val id = resultItemObject.get("id").asInt
                                val url = resultItemObject.get("url").asString
                                val createdAt = resultItemObject.get("createdAt").asString
                                val updatedAt = resultItemObject.get("updatedAt").asString
                                val snsImageItem = SnsImage(
                                    id,
                                    url,
                                    createdAt,
                                    updatedAt,
                                )
                                parsedSnsImageDataArray.add(snsImageItem)
                            }
                            completion(RESPONSE_STATUS.OKAY, parsedSnsImageDataArray)
                        }
                    }
                    400 -> {
                        Log.d("[debug]", response.body().toString())
                    }
                }
            }
        })
    }


    //SNS에서 포스트에 해당하는 좋아요를 저장하는 함수
    fun postPostLike(
        postId: Int,
        user: String,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {

        val call =
            snsService?.postPostLike(
                SnsPostLikeRequestBodyParams(postId, user),
            ) ?: return

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


    fun deletePostLike(
        postId: Int,
        user: String,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {

        val call =
            snsService?.deletePostLike(
                postId,
                user
            ) ?: return

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


    //SNS에서 포스트에 해당하는 좋아요를 저장하는 함수
    fun postCommentLike(
        commentId: Int,
        user: String,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {

        val call =
            snsService?.postCommentLike(
                SnsCommentLikeRequestBodyParams(commentId, user),
            ) ?: return

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


    fun deleteCommentLike(
        commentId: Int,
        user: String,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {

        val call =
            snsService?.deleteCommentLike(
                commentId,
                user
            ) ?: return

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


    //프로필 사진 추가
    fun postProfile(
        userId: Int,
        images: ArrayList<MultipartBody.Part>?,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {

        val call =
            snsService?.postProfile(userId, images) ?: return

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


}