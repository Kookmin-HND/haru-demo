package com.example.harudemo.todo

import com.example.harudemo.retrofit.TodoRetrofitManager
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.TodoLog
import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonElement

object TodoData {
    object API {
        // DB에 todo를 추가한다.
        // 동시에 todo-log도 추가된다.
        fun create(
            writer: String,
            folder: String,
            content: String,
            dates: List<String>,
            days: List<Boolean>,
            okayCallback: (JsonElement) -> Unit = {},
            failCallback: (JsonElement) -> Unit = {},
            noContentCallback: (JsonElement) -> Unit = {}
        ) {
            TodoRetrofitManager.instance.addTodo(
                writer,
                folder,
                content,
                dates,
                days,
                completion = { responseStatus, response ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> response?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> response?.let { failCallback(it) }
                        RESPONSE_STATUS.NO_CONTENT -> response?.let { noContentCallback(it) }
                    }
                })
        }

        // 사용자의 모든 todo를 반환한다.
        // completed 값에 따라 완료여부 값들을 필터링한다.
        fun getTodos(
            writer: String,
            completed: Boolean,
            okayCallback: (List<Todo>) -> Unit = {},
            failCallback: () -> Unit = {},
            noContentCallback: () -> Unit = {}
        ) {
            // 만약 유저가 유저의 TodoData를 가지고 있지 않으면 불러온다.
            TodoRetrofitManager.instance.getTodos(
                writer,
                completed,
                completion = { responseStatus, response ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> response?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> failCallback()
                        RESPONSE_STATUS.NO_CONTENT -> noContentCallback()
                    }
                })
        }

        // 사용자의 모든 데이터를 가져오기 (todo-log) 포함
        fun getTodosAndTodoLogs(
            writer: String,
            okayCallback: (Pair<Todo, ArrayList<TodoLog>>) -> Unit = {},
            failCallback: () -> Unit = {},
            noContentCallback: () -> Unit = {},
        ) {
            TodoRetrofitManager.instance.getTodosAndTodoLogs(
                writer,
                completion = { responseStatus, pair ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> pair?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> failCallback()
                        RESPONSE_STATUS.NO_CONTENT -> noContentCallback()
                    }
                })
        }

        // 사용자가 작성한 todo 중 folder가 일치하는 todo를 반환한다.
        // completed 값에 따라 완료여부를 필터링한다.
        fun getTodosByFolder(
            writer: String,
            folder: String,
            completed: Boolean,
            okayCallback: (response: ArrayList<Todo>) -> Unit = {},
            failCallback: () -> Unit = {},
            noContentCallback: () -> Unit = {},
        ) {
            TodoRetrofitManager.instance.getTodosByFolder(
                writer,
                folder,
                completed,
                completion = { responseStatus, todos ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> todos?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> failCallback()
                        RESPONSE_STATUS.NO_CONTENT -> noContentCallback()
                    }
                }
            )
        }

        // 사용자가 작성한 todo 중 date가 일치하는 모든 todo를 반환한다.
        // completed 값에 따라 완료여부를 필터링한다.
        fun getTodosByDate(
            writer: String,
            date: String,
            completed: Boolean,
            okayCallback: (response: ArrayList<Todo>) -> Unit = {},
            failCallback: () -> Unit = {},
            noContentCallback: () -> Unit = {},
        ) {
            TodoRetrofitManager.instance.getTodosByDate(
                writer, date, completed,
                completion = { responseStatus, todos ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> todos?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> failCallback()
                        RESPONSE_STATUS.NO_CONTENT -> noContentCallback()
                    }
                }
            )
        }

        // DB에서 일치하는 todo를 업데이트한다.
        fun update(
            id: Number,
            folder: String,
            content: String,
            dates: List<String>,
            days: List<Boolean>,
            okayCallback: (response: JsonElement) -> Unit = {},
            failCallback: (response: JsonElement) -> Unit = {},
            noContentCallback: (response: JsonElement) -> Unit = {},
        ) {
            TodoRetrofitManager.instance.updateTodo(id,
                folder,
                content,
                dates,
                days,
                completion = { responseStatus, response ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> response?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> response?.let { failCallback(it) }
                        RESPONSE_STATUS.NO_CONTENT -> response?.let { noContentCallback(it) }
                    }
                })
        }

        // todo-logs DB에 접근하여 입력으로 받은 completed 값으로 바꾸어준다.
        fun checkTodo(
            todoId: Number,
            date: String,
            completed: Boolean,
            okayCallback: (response: JsonElement) -> Unit = {},
            failCallback: (response: JsonElement) -> Unit = {},
            noContentCallback: (response: JsonElement) -> Unit = {},
        ) {
            TodoRetrofitManager.instance.checkTodo(
                todoId, date, completed, completion = { responseStatus, response ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> response?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> response?.let { failCallback(it) }
                        RESPONSE_STATUS.NO_CONTENT -> response?.let { noContentCallback(it) }
                    }
                }
            )
        }

        // todo-logs DB에 접근하여 입력으로 받은 completed 값으로 바꾸어준다.
        fun delete(
            id: Number,
            okayCallback: (response: JsonElement) -> Unit = {},
            failCallback: (response: JsonElement) -> Unit = {},
            noContentCallback: (response: JsonElement) -> Unit = {},
        ) {
            TodoRetrofitManager.instance.deleteTodo(
                id,
                completion = { responseStatus, response ->
                    when (responseStatus) {
                        RESPONSE_STATUS.OKAY -> response?.let { okayCallback(it) }
                        RESPONSE_STATUS.FAIL -> response?.let { failCallback(it) }
                        RESPONSE_STATUS.NO_CONTENT -> response?.let { noContentCallback(it) }
                    }
                })
        }
    }
}