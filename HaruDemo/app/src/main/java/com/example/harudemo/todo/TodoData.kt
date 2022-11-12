package com.example.harudemo.todo

import com.example.harudemo.retrofit.TodoRetrofitManager
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonElement

object TodoData {
    object API {
        // DB에 todo를 추가한다.
        fun create(
            writer: String,
            folder: String,
            content: String,
            dates: List<String>,
            okayCallback: (todos: List<Todo>) -> Unit = {},
            failCallback: () -> Unit = {},
            noContentCallback: () -> Unit = {}
        ) {
            TodoRetrofitManager.instance.addTodo(
                writer,
                folder,
                content,
                dates,
                completion = { responseStatus, todos ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> {
                            // DB에 데이터를 넣는 것에 성공하면 해당 응답을 받아서, 이를 다시 배열로 만들어 반환한다.
                            val response = arrayListOf<Todo>()
                            if (todos != null) {
                                for (i in 0 until todos.size()) {
                                    val todo = todos[i].asJsonObject
                                    response.add(
                                        Todo(
                                            todo.get("id").asInt,
                                            todo.get("writer").asString,
                                            todo.get("folder").asString,
                                            todo.get("content").asString,
                                            todo.get("date").asString,
                                            todo.get("completed").asBoolean,
                                        )
                                    )
                                }
                            }

                            okayCallback(response)
                        }
                        RESPONSE_STATUS.FAIL -> failCallback()
                        RESPONSE_STATUS.NO_CONTENT -> noContentCallback()
                    }
                })
        }

        // DB에서 writer, completed가 일치하는 데이터를 불러온다.
        fun getTodos(
            writer: String,
            completed: Boolean,
            okayCallback: (todos: List<Todo>) -> Unit = {},
            failCallback: () -> Unit = {},
            noContentCallback: () -> Unit = {}
        ) {
            // 만약 유저가 유저의 TodoData를 가지고 있지 않으면 불러온다.
            TodoRetrofitManager.instance.getTodos(
                writer,
                completed,
                completion = { responseStatus, todos ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> todos?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> failCallback()
                        RESPONSE_STATUS.NO_CONTENT -> noContentCallback()
                    }
                })
        }

        // DB에서 일치하는 todo를 업데이트한다.
        fun update(
            id: Number,
            folder: String,
            content: String,
            date: String,
            completed: Boolean,
            okayCallback: (response: JsonElement) -> Unit = {},
            failCallback: (response: JsonElement) -> Unit = {},
            noContentCallback: (response: JsonElement) -> Unit = {},
        ) {
            TodoRetrofitManager.instance.updateTodo(id,
                folder,
                content,
                date,
                completed,
                completion = { responseStatus, jsonElement ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> jsonElement?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> jsonElement?.let { failCallback(it) }
                        RESPONSE_STATUS.NO_CONTENT -> jsonElement?.let { noContentCallback(it) }
                    }
                })
        }

        // DB에서 일치하는 todo를 제거한다.
        fun delete(
            id: Number,
            okayCallback: (response: JsonElement) -> Unit = {},
            failCallback: (response: JsonElement) -> Unit = {},
            noContentCallback: (response: JsonElement) -> Unit = {},
        ) {
            TodoRetrofitManager.instance.deleteTodo(
                id,
                completion = { responseStatus, jsonElement ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> jsonElement?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> jsonElement?.let { failCallback(it) }
                        RESPONSE_STATUS.NO_CONTENT -> jsonElement?.let { noContentCallback(it) }
                    }
                })
        }
    }
}