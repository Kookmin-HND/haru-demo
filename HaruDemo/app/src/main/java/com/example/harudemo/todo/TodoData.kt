package com.example.harudemo.todo

import android.util.Log
import com.example.harudemo.retrofit.RetrofitManager
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.utils.RESPONSE_STATUS

object TodoData {
    val todos: ArrayList<Todo> = ArrayList()
    val folderNames: MutableSet<String> = mutableSetOf()

    fun addTodo(
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
                        val _todos = arrayListOf<Todo>()
                        if (todos != null) {
                            for (i in 0 until todos.size()) {
                                val todo = todos[i].asJsonObject
                                _todos.add(
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
                        okayCallback(_todos)
                    }
                    RESPONSE_STATUS.FAIL -> failCallback()
                    RESPONSE_STATUS.NO_CONTENT -> noContentCallback()
                }
            })
    }

    // DB에서 writer가 일치하는 Todo Data를 불러온다.
    fun fetchTodos(
        writer: String,
        okayCallback: (todos: List<Todo>) -> Unit = {},
        failCallback: () -> Unit = {},
        noContentCallback: () -> Unit = {}
    ) {
        // 만약 유저가 유저의 todo data를 가지고 있지 않으면 불러온다.
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

    fun getTodosByFolder(folderName: String): List<Section> {
        val todos = arrayListOf<Todo>()
        for (todo in this.todos) {
            if (todo.folder == folderName) {
                todos.add(todo)
            }
        }
        return listOf(Section(folderName, todos))
    }

    fun getTodos(completed: Boolean = false): List<Section> {
        val todos = mutableMapOf<String, ArrayList<Todo>>()
        for (todo in this.todos) {
            if (completed && !todo.completed) {
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

        Log.d("[debug]", dates.toString())
        Log.d("[debug]", todos.toString())
        for (todo in this.todos) {
            if (todo.date in todos) {
                todos[todo.date]?.add(todo)
            }
        }

        todos.values.removeIf { it.isEmpty() }
        return todos.map {
            Section(it.key, it.value)
        }
    }
}