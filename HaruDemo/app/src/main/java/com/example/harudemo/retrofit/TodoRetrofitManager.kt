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
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d("[debug]", t.toString())
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }

    // 사용자의 모든 todo를 반환한다.
    // completed 값에 따라 완료여부 값들을 필터링한다.
    fun getTodos(
        writer: String,
        completed: Boolean,
        completion: (RESPONSE_STATUS, HashMap<Number, Pair<Todo, ArrayList<TodoLog>>>?) -> Unit
    ) {
        val call = todoService?.getTodos(writer, completed) ?: return
        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                when (response.code()) {
                    200 -> {
                        val result: HashMap<Number, Pair<Todo, ArrayList<TodoLog>>> = hashMapOf()
                        val responseBody = response.body() ?: return
                        for (todoId in responseBody.keySet()) {
                            val todoObject =
                                responseBody.get(todoId).asJsonObject.get("todo").asJsonObject
                            val id = todoObject.get("id").asInt
                            val writer = todoObject.get("writer").asString
                            val folder = todoObject.get("folder").asString
                            val content = todoObject.get("content").asString
                            val rawDays = todoObject.get("days").asString
                            val days =
                                todoObject.get("days").asString.slice(1 until rawDays.length - 1)
                                    .split(',').map { it == "true" }

                            val logs = ArrayList<TodoLog>()
                            for (logsElement in responseBody.get(todoId).asJsonObject.get("logs").asJsonArray) {
                                for (logElement in logsElement.asJsonArray) {
                                    val logObject = logElement.asJsonObject
                                    val id = logObject.get("id").asInt
                                    val todoId = logObject.get("todoId").asInt
                                    val date = logObject.get("date").asString
                                    val completed = logObject.get("completed").asBoolean
                                    logs.add(TodoLog(id, todoId, date, completed))
                                }
                            }
                            result[todoId.toInt()] =
                                Pair(Todo(id, writer, folder, content, days), logs)
                        }
                        completion(RESPONSE_STATUS.OKAY, result)
                    }
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {
                Log.d("[debug]", t.toString())
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }

    // 사용자가 가지고 있는 todo를 folder로 구분하여 반환한다.
    fun getAllTodosByFolder(
        writer: String,
        completed: Boolean,
        completion: (RESPONSE_STATUS, HashMap<String, Pair<ArrayList<Todo>, ArrayList<ArrayList<TodoLog>>>>?) -> Unit
    ) {
        val call =
            todoService?.getAllTodosByFolder(writer, completed) ?: return
        call.enqueue(object :
            retrofit2.Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                when (response.code()) {
                    200 -> {
                        val responseBody = response.body() ?: return
                        val result: HashMap<String, Pair<ArrayList<Todo>, ArrayList<ArrayList<TodoLog>>>> =
                            hashMapOf()
                        for (title in responseBody.keySet()) {
                            result[title] = Pair(arrayListOf(), arrayListOf())
                            // todo 추가
                            for (todoElement in responseBody.get(title).asJsonObject.get("todos").asJsonArray) {
                                val todoObject = todoElement.asJsonObject
                                val id = todoObject.get("id").asInt
                                val writer = todoObject.get("writer").asString
                                val folder = todoObject.get("folder").asString
                                val content = todoObject.get("content").asString
                                val rawDays = todoObject.get("days").asString
                                val days =
                                    todoObject.get("days").asString.slice(1 until rawDays.length - 1)
                                        .split(',').map { it == "true" }
                                result[title]?.first?.add(Todo(id, writer, folder, content, days))
                            }
                            // logs 추가
                            for (logsElement in responseBody.get(title).asJsonObject.get("logs").asJsonArray) {
                                val logs = ArrayList<TodoLog>()
                                for (logElement in logsElement.asJsonArray) {
                                    val logObject = logElement.asJsonObject
                                    val id = logObject.get("id").asInt
                                    val todoId = logObject.get("todoId").asInt
                                    val date = logObject.get("date").asString
                                    val completed = logObject.get("completed").asBoolean
                                    logs.add(TodoLog(id, todoId, date, completed))
                                }
                                result[title]?.second?.add(logs)
                            }
                        }
                        completion(RESPONSE_STATUS.OKAY, result)
                    }
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {
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
        completion: (RESPONSE_STATUS, Pair<ArrayList<Todo>, ArrayList<ArrayList<TodoLog>>>?) -> Unit
    ) {
        val call =
            todoService?.getTodosByFolder(writer, folder, completed) ?: return
        call.enqueue(object :
            retrofit2.Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                when (response.code()) {
                    200 -> {
                        val result: Pair<ArrayList<Todo>, ArrayList<ArrayList<TodoLog>>> = Pair(
                            arrayListOf(), arrayListOf()
                        )
                        val responseBody = response.body() ?: return
                        // todos 추가.
                        for (todoElement in responseBody.get("todos").asJsonArray) {
                            val todoObject = todoElement.asJsonObject
                            val id = todoObject.get("id").asInt
                            val writer = todoObject.get("writer").asString
                            val folder = todoObject.get("folder").asString
                            val content = todoObject.get("content").asString
                            val rawDays = todoObject.get("days").asString
                            val days =
                                todoObject.get("days").asString.slice(1 until rawDays.length - 1)
                                    .split(',').map { it == "true" }
                            result.first.add(Todo(id, writer, folder, content, days))
                        }
                        // logs 추가.
                        for (logsElement in responseBody.get("logs").asJsonArray) {
                            val logs = ArrayList<TodoLog>()
                            for (logElement in logsElement.asJsonArray) {
                                val logObject = logElement.asJsonObject
                                val id = logObject.get("id").asInt
                                val todoId = logObject.get("todoId").asInt
                                val date = logObject.get("date").asString
                                val completed = logObject.get("completed").asBoolean
                                logs.add(TodoLog(id, todoId, date, completed))
                            }
                            result.second.add(logs)
                        }
                        completion(RESPONSE_STATUS.OKAY, result)
                    }
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {
                Log.d("[debug]", t.toString())
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
        completion: (RESPONSE_STATUS, HashMap<String, Pair<ArrayList<Todo>, ArrayList<TodoLog>>>?) -> Unit
    ) {
        val call =
            todoService?.getTodosByDateInDates(writer, completed, dates)
                ?: return
        call.enqueue(object :
            retrofit2.Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                when (response.code()) {
                    200 -> {
                        val result: HashMap<String, Pair<ArrayList<Todo>, ArrayList<TodoLog>>> =
                            hashMapOf()
                        val responseBody = response.body() ?: return
                        for (date in responseBody.keySet()) {
                            result[date] = Pair(arrayListOf(), arrayListOf())
                            // todos 추가
                            for (todoElement in responseBody.get(date).asJsonObject.get("todos").asJsonArray) {
                                val todoObject = todoElement.asJsonObject
                                val id = todoObject.get("id").asInt
                                val writer = todoObject.get("writer").asString
                                val folder = todoObject.get("folder").asString
                                val content = todoObject.get("content").asString
                                val rawDays = todoObject.get("days").asString
                                val days =
                                    todoObject.get("days").asString.slice(1 until rawDays.length - 1)
                                        .split(',').map { it == "true" }
                                result[date]?.first?.add(Todo(id, writer, folder, content, days))
                            }
                            // logs 추가
                            for (logElement in responseBody.get(date).asJsonObject.get("logs").asJsonArray) {
                                val logObject = logElement.asJsonObject
                                val id = logObject.get("id").asInt
                                val todoId = logObject.get("todoId").asInt
                                val date = logObject.get("date").asString
                                val completed = logObject.get("completed").asBoolean
                                result[date]?.second?.add(TodoLog(id, todoId, date, completed))
                            }
                        }
                        completion(RESPONSE_STATUS.OKAY, result)
                    }
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {
                Log.d("[debug]", t.toString())
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
        completion: (RESPONSE_STATUS, Pair<ArrayList<Todo>, ArrayList<TodoLog>>?) -> Unit
    ) {
        val call =
            todoService?.getTodosByDate(writer, date, completed) ?: return
        call.enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                when (response.code()) {
                    200 -> {
                        val result: Pair<ArrayList<Todo>, ArrayList<TodoLog>> =
                            Pair(arrayListOf(), arrayListOf())
                        val responseBody = response.body() ?: return
                        // todos 추가
                        for (todoElement in responseBody.get("todos").asJsonArray) {
                            val todoObject = todoElement.asJsonObject
                            val id = todoObject.get("id").asInt
                            val writer = todoObject.get("writer").asString
                            val folder = todoObject.get("folder").asString
                            val content = todoObject.get("content").asString
                            val rawDays = todoObject.get("days").asString
                            val days =
                                todoObject.get("days").asString.slice(1 until rawDays.length - 1)
                                    .split(',').map { it == "true" }
                            result.first.add(Todo(id, writer, folder, content, days))
                        }
                        // logs 추가
                        for (logElement in responseBody.get("logs").asJsonArray) {
                            val logObject = logElement.asJsonObject
                            val id = logObject.get("id").asInt
                            val todoId = logObject.get("todoId").asInt
                            val date = logObject.get("date").asString
                            val completed = logObject.get("completed").asBoolean
                            result.second.add(TodoLog(id, todoId, date, completed))
                        }
                        completion(RESPONSE_STATUS.OKAY, result)
                    }
                }
            }

            override fun onFailure(
                call: Call<JsonObject>,
                t: Throwable
            ) {
                Log.d("[debug]", t.toString())
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
                Log.d("[debug]", t.toString())
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
                Log.d("[debug]", t.toString())
                completion(RESPONSE_STATUS.FAIL, null)
            }
        })
    }
}