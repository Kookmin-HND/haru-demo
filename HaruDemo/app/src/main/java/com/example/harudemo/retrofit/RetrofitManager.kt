package com.example.harudemo.retrofit

import android.util.Log
import com.example.harudemo.model.SnsPost
import com.example.harudemo.utils.API
import com.example.harudemo.utils.Constants.TAG
import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {
    companion object {
        val instance = RetrofitManager()
    }

    private val snsService: SnsService? =
        RetrofitClient.getClient(API.BASE_URL)?.create(SnsService::class.java)
    private val todoService: TodoService? =
        RetrofitClient.getClient(API.BASE_URL)?.create(TodoService::class.java)

    //sns Test용 getPosts 함수 - SNS에서 게시물을 호출하는 함수
    fun getPosts(
        completion: (RESPONSE_STATUS, ArrayList<SnsPost>?) -> Unit
    ) {

        val call = snsService?.getPosts().let {
            it
        } ?: return


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
                                val userId = resultItemObject.get("userId").asInt.toString()
                                val postId = resultItemObject.get("id").asInt.toString()
                                val postTitle = resultItemObject.get("title").asString
                                val postBody = resultItemObject.get("body").asString

                                val snsPostItem = SnsPost(
                                    writer = postTitle,
                                    content = postBody,
                                    createdAt = "2022.10.07",
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

    // DB에서 Todo Data를 불러온다.
    fun getTodos(writer: String, completion: (RESPONSE_STATUS, ArrayList<SnsPost>?) -> Unit) {
        val call = todoService?.getTodos(writer) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    // STATUS 200
                    200 -> {
                        val responseBody = response.body() ?: return
                        val todos = responseBody.asJsonArray

                        todos.forEach { todoJson ->
                            val todo = todoJson.asJsonObject
                            val writer = todo.get("writer").asString
                            val folder = todo.get("folder").asString
                            val content = todo.get("content").asString
                            val date = todo.get("date").asString
                            val completed = todo.get("completed").asBoolean
                            val createdAt = todo.get("createdAt").asJsonObject
                            val updatedAt = todo.get("updatedAt").asJsonObject

                        }
                    }
                }
            }
        })
    }
}