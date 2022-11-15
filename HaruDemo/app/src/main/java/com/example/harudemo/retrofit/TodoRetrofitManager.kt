package com.example.harudemo.retrofit

import android.util.Log
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.TodoLog
import com.example.harudemo.utils.API
import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

class TodoRetrofitManager {
    companion object {
        val instance = TodoRetrofitManager()
    }

    private val todoService: TodoService? =
        RetrofitClient.getClient()?.create(TodoService::class.java)

    // DB에 TodoData를 추가한다.
    fun addTodo(
        writer: String,
        folder: String,
        content: String,
        dates: List<String>,
        days: List<Boolean>,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {
        val call =
            todoService?.addTodos(writer, PostRequestBodyParams(folder, content, dates, days))
                ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        completion(RESPONSE_STATUS.OKAY, response.body())
                    }
                    400 -> {
                        Log.d("[debug]", response.body().toString())
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

        })
    }

    // 사용자의 모든 todo를 반환한다.
    // completed 값에 따라 완료여부 값들을 필터링한다.
    fun getTodos(
        writer: String,
        completed: Boolean,
        completion: (RESPONSE_STATUS, ArrayList<Todo>?) -> Unit
    ) {
        val call = todoService?.getTodos(writer, GetRequestBodyParams(completed)) ?: return
        call.enqueue(object : retrofit2.Callback<ArrayList<Todo>> {
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

            override fun onFailure(call: Call<ArrayList<Todo>>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }

    // 사용자의 모든 데이터를 가져오기 (todo-log) 포함
    fun getTodosAndTodoLogs(
        writer: String,
        completion: (RESPONSE_STATUS, Pair<Todo, ArrayList<TodoLog>>?) -> Unit
    ) {
        val call = todoService?.getTodosAndTodoLogs(writer) ?: return
        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                when (response.code()) {
                    200 -> {
                        val body = response.body()
                        if (body == null) {
                            completion(RESPONSE_STATUS.FAIL, null)
                        } else {
                            val rawTodo = body.get("todo").asJsonObject
                            val rawTodoLogs = body.get("todoLogs").asJsonArray

                            val todo = Todo(
                                rawTodo.get("id").asInt,
                                rawTodo.get("writer").asString,
                                rawTodo.get("folder").asString,
                                rawTodo.get("content").asString
                            )
                            val todoLogs: ArrayList<TodoLog> = arrayListOf()
                            for (log in rawTodoLogs) {
                                val logObj = log.asJsonObject
                                todoLogs.add(
                                    TodoLog(
                                        id = logObj.get("id").asInt,
                                        todoId = logObj.get("todoId").asInt,
                                        date = logObj.get("date").asString,
                                        completed = logObj.get("completed").asBoolean
                                    )
                                )
                            }
                            completion(RESPONSE_STATUS.OKAY, Pair(todo, todoLogs))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }

    // 사용자로부터 todoId를 입력받아, 해당 todo의 todo-log를 반환한다.
    fun getLogs(
        todoId: Number,
        completed: Boolean,
        completion: (RESPONSE_STATUS, ArrayList<TodoLog>?) -> Unit
    ): Response<ArrayList<TodoLog>>? {
        val call = todoService?.getLogs(todoId, completed)
        return call?.execute()
    }

    // 사용자로부터 todoId를 입력받아, 해당 todo의 completed 상관없이 모두 반환한다.
    fun getLogsAll(
        todoId: Number,
        completion: (RESPONSE_STATUS, ArrayList<TodoLog>?) -> Unit
    ): Response<ArrayList<TodoLog>>? {
        val call = todoService?.getLogsAll(todoId)
        return call?.execute()
    }

    // 사용자가 가지고 있는 todo를 folder로 구분하여 반환한다.
    fun getAllTodosByFolder(
        writer: String,
        completed: Boolean,
        completion: (RESPONSE_STATUS, HashMap<String, ArrayList<Todo>>?) -> Unit
    ) {
        val call =
            todoService?.getAllTodosByFolder(writer, GetRequestBodyParams(completed)) ?: return
        call.enqueue(object : retrofit2.Callback<HashMap<String, ArrayList<Todo>>> {
            override fun onResponse(
                call: Call<HashMap<String, ArrayList<Todo>>>,
                response: Response<HashMap<String, ArrayList<Todo>>>
            ) {
                when (response.code()) {
                    200 -> {
                        val todosByFolder = response.body() ?: return
                        completion(RESPONSE_STATUS.OKAY, todosByFolder)
                    }
                }
            }

            override fun onFailure(call: Call<HashMap<String, ArrayList<Todo>>>, t: Throwable) {
                Log.d("[debug]", t.toString())
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }

    // 사용자가 작성한 todo 중 folder가 일치하는 todo를 반환한다.
    // completed 값에 따라 완료여부를 필터링한다.
    fun getTodosByFolder(
        writer: String,
        folder: String,
        completed: Boolean,
        completion: (RESPONSE_STATUS, ArrayList<Todo>?) -> Unit
    ) {
        val call =
            todoService?.getTodosByFolder(writer, folder, GetRequestBodyParams(completed)) ?: return
        call.enqueue(object : retrofit2.Callback<ArrayList<Todo>> {
            override fun onResponse(
                call: Call<ArrayList<Todo>>,
                response: Response<ArrayList<Todo>>
            ) {
                when (response.code()) {
                    200 -> {
                        val todos = response.body() ?: return
                        completion(RESPONSE_STATUS.OKAY, todos)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Todo>>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }

    // 사용자가 작성한 todo 중 date가 일치하는 모든 todo를 반환한다.
    // completed 값에 따라 완료여부를 필터링한다.
    fun getTodosByDateInDates(
        writer: String,
        dates: List<String>,
        completed: Boolean,
        completion: (RESPONSE_STATUS, HashMap<String, ArrayList<Todo>>?) -> Unit
    ) {
        val call =
            todoService?.getTodosByDateInDates(writer, GetRequestBodyParams(completed, dates))
                ?: return
        call.enqueue(object : retrofit2.Callback<HashMap<String, ArrayList<Todo>>> {
            override fun onResponse(
                call: Call<HashMap<String, ArrayList<Todo>>>,
                response: Response<HashMap<String, ArrayList<Todo>>>
            ) {
                when (response.code()) {
                    200 -> {
                        val todosByDate = response.body() ?: return
                        completion(RESPONSE_STATUS.OKAY, todosByDate)
                    }
                }
            }

            override fun onFailure(call: Call<HashMap<String, ArrayList<Todo>>>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }

    // 사용자가 작성한 todo 중 date가 일치하는 모든 todo를 반환한다.
    // completed 값에 따라 완료여부를 필터링한다.
    fun getTodosByDate(
        writer: String,
        date: String,
        completed: Boolean,
        completion: (RESPONSE_STATUS, ArrayList<Todo>?) -> Unit
    ) {
        val call =
            todoService?.getTodosByDate(writer, date, GetRequestBodyParams(completed)) ?: return
        call.enqueue(object : retrofit2.Callback<ArrayList<Todo>> {
            override fun onResponse(
                call: Call<ArrayList<Todo>>,
                response: Response<ArrayList<Todo>>
            ) {
                when (response.code()) {
                    200 -> {
                        val todos = response.body() ?: return
                        completion(RESPONSE_STATUS.OKAY, todos)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Todo>>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }

    // DB에서 TodoData를 업데이트한다.
    fun updateTodo(
        id: Number,
        folder: String,
        content: String,
        dates: List<String>,
        days: List<Boolean>,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {
        val call =
            todoService?.updateTodo(PatchRequestBodyParams(id, folder, content, dates, days))
                ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        completion(RESPONSE_STATUS.OKAY, response.body())
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }

        })
    }

    // todo-logs DB에 접근하여 입력으로 받은 completed 값으로 바꾸어준다.
    fun checkTodo(
        todoId: Number,
        date: String,
        completed: Boolean,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {
        val call = todoService?.checkTodo(CheckRequestBodyParams(todoId, date, completed)) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        completion(RESPONSE_STATUS.OKAY, response.body())
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }

    // 사용자로부터 todo id값을 입력받아 해당 데이터를 삭제한다.
    // 그리고 todo-logs에 접근하여 해당하는 log를 모두 삭제한다.
    fun deleteTodo(
        id: Number,
        completion: (RESPONSE_STATUS, JsonElement?) -> Unit
    ) {
        val call = todoService?.deleteTodo(DeleteRequestBodyParams(id)) ?: return
        call.enqueue(object : retrofit2.Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                when (response.code()) {
                    200 -> {
                        completion(RESPONSE_STATUS.OKAY, response.body())
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }
}