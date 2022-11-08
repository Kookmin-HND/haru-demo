package com.example.harudemo.retrofit

import android.util.Log
import com.example.harudemo.model.SnsPost
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.utils.API
import com.example.harudemo.utils.Constants.TAG
import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonArray
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
                                    average = "",
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

    // DB에서 TodoData를 불러온다.
    fun getTodos(writer: String, completion: (RESPONSE_STATUS, ArrayList<Todo>?) -> Unit) {
        val call = todoService?.getTodos(writer) ?: return

        call.enqueue(object : retrofit2.Callback<ArrayList<Todo>> {
            override fun onFailure(call: Call<ArrayList<Todo>>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(
                call: Call<ArrayList<Todo>>, response: Response<ArrayList<Todo>>
            ) {
                when (response.code()) {
                    200 -> {
                        val todos = response.body() ?: return
                        completion(RESPONSE_STATUS.OKAY, todos)
                    }
                }
            }
        })
    }

    // DB에 TodoData를 추가한다.
    fun addTodo(
        writer: String,
        folder: String,
        content: String,
        dates: List<String>,
        completion: (RESPONSE_STATUS, JsonArray?) -> Unit
    ) {
        val call =
            todoService?.addTodos(writer, PostRequestBodyParams(folder, content, dates)) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        completion(RESPONSE_STATUS.OKAY, response.body()?.asJsonArray)
                    }
                    400 -> {
                        Log.d("[debug]", response.body().toString())
                    }
                }
            }
        })
    }

    // DB에서 TodoData를 업데이트한다.
    fun updateTodo(
        id: Number,
        folder: String,
        content: String,
        date: String,
        completed: Boolean,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {
        val call =
            todoService?.updateTodo(PatchRequestBodyParams(id, folder, content, date, completed))
                ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        completion(RESPONSE_STATUS.OKAY, response.body())
                    }
                }
            }
        })
    }

    // DB에서 TodoData를 삭제한다.
    fun deleteTodo(
        id: Number,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {
        val call = todoService?.deleteTodo(DeleteRequestBodyParams(id)) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        completion(RESPONSE_STATUS.OKAY, response.body())
                    }
                }
            }
        })
    }
}