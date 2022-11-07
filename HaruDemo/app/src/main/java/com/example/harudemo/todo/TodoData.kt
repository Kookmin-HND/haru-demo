package com.example.harudemo.todo

import android.util.Log
import com.example.harudemo.retrofit.RetrofitManager
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.utils.RESPONSE_STATUS
import com.google.gson.JsonElement

object TodoData {
    // todos, todosByFolder 내에 있는 TodoData는 같은 instance를 가진다.
    private val todos: ArrayList<Todo> = ArrayList()
    private val todosByFolder: HashMap<String, ArrayList<Todo>> = HashMap()

    // todos, todosByFolder에 TodoData를 추가
    fun add(todo: Todo) {
        todos.add(todo)
        if (todo.folder in todosByFolder) {
            todosByFolder[todo.folder]?.add(todo)
        } else {
            todosByFolder[todo.folder] = arrayListOf(todo)
        }
    }

    fun get(folder: String): List<Todo>? {
        return todosByFolder[folder]
    }

    // TodoData가 이전과 다른 폴더를 가진다면 이를 변경
    private fun changeFolder(todo: Todo, folder: String) {
        todosByFolder[todo.folder]?.removeIf { it.id.toInt() == todo.id.toInt() }
        if (folder in todosByFolder) {
            todosByFolder[folder]?.add(todo)
        } else {
            todosByFolder[folder] = arrayListOf(todo)
        }
        todo.folder = folder
    }

    // 데이터에 접근해서 입력받은 데이터로 변경
    fun update(
        todo: Todo,
        folder: String? = null,
        content: String? = null,
        date: String? = null,
        completed: Boolean? = null
    ) {
        val updated = todosByFolder[todo.folder]?.find { it.id.toInt() == todo.id.toInt() }
        if (folder != null) {
            if (updated?.folder != folder) {
                changeFolder(todo, folder)
            }
            updated?.folder = folder
        }
        if (content != null) {
            updated?.content = content
        }
        if (date != null) {
            updated?.date = date
        }
        if (completed != null) {
            updated?.completed = completed
        }
    }

    // 데이터 삭제 (todos, todosByFolder)에서 모두 삭제
    fun delete(todo: Todo) {
        todos.removeIf { it.id.toInt() == todo.id.toInt() }
        todosByFolder[todo.folder]?.removeIf { it.id.toInt() == todo.id.toInt() }
        if (todosByFolder[todo.folder]?.isEmpty() == true) {
            todosByFolder.remove(todo.folder)
        }
    }

    fun getTodosByFolder(folderName: String): List<Section> {
        val todos = arrayListOf<Todo>()
        for (todo in this.todos) {
            if (todo.folder == folderName && !todo.completed) {
                todos.add(todo)
            }
        }
        if (todos.isEmpty()) {
            return listOf()
        }
        return listOf(Section(folderName, todos))
    }

    fun getTodos(completed: Boolean = false): List<Section> {
        val todos = mutableMapOf<String, ArrayList<Todo>>()
        for (todo in this.todos) {
            if (completed != todo.completed) {
                continue
            }

            if (todo.folder in todos) {
                todos[todo.folder]?.add(todo)
            } else {
                todos[todo.folder] = arrayListOf(todo)
            }
        }

        todos.values.removeIf { it.isEmpty() }
        return todos.map {
            Section(it.key, it.value)
        }
    }

    fun getTodosByDates(dates: List<String>): List<Section> {
        val todos = mutableMapOf<String, ArrayList<Todo>>()
        for (date in dates) {
            todos[date] = arrayListOf()
        }

        for (todo in this.todos) {
            if (todo.completed) {
                continue
            }

            if (todo.date in todos) {
                todos[todo.date]?.add(todo)
            }
        }

        todos.values.removeIf { it.isEmpty() }
        return todos.map {
            Section(it.key, it.value)
        }
    }

    fun isEmpty(): Boolean {
        return todos.isEmpty()
    }

    fun getFolderNames(): Set<String> {
        return todosByFolder.keys
    }

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
            RetrofitManager.instance.addTodo(writer,
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

        // DB에서 writer가 일치하는 TodoData를 불러온다.
        fun read(
            writer: String,
            okayCallback: (todos: List<Todo>) -> Unit = {},
            failCallback: () -> Unit = {},
            noContentCallback: () -> Unit = {}
        ) {
            // 만약 유저가 유저의 TodoData를 가지고 있지 않으면 불러온다.
            RetrofitManager.instance.getTodos(writer, completion = { responseStatus, todos ->
                when (responseStatus) {
                    RESPONSE_STATUS.OKAY -> {
                        if (todos != null) {
                            okayCallback(todos)
                        }
                    }
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
            RetrofitManager.instance.updateTodo(
                id,
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
            RetrofitManager.instance.deleteTodo(id, completion = { responseStatus, jsonElement ->
                when (responseStatus) {
                    RESPONSE_STATUS.OKAY -> jsonElement?.let { okayCallback(it) }
                    RESPONSE_STATUS.FAIL -> jsonElement?.let { failCallback(it) }
                    RESPONSE_STATUS.NO_CONTENT -> jsonElement?.let { noContentCallback(it) }
                }
            })
        }
    }
}