package com.example.harudemo.todo

import android.util.Log
import com.example.harudemo.retrofit.RetrofitManager
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.utils.RESPONSE_STATUS

object TodoData {
    val todos: ArrayList<Todo> = ArrayList()
    val folderNames: MutableSet<String> = mutableSetOf()

    fun fetchTodos(
        okayCallback: (todos: ArrayList<Todo>) -> Unit,
        failCallback: () -> Unit,
        noContentCallback: () -> Unit
    ) {
        // 만약 유저가 유저의 todo data를 가지고 있지 않으면 불러온다.
        RetrofitManager.instance.getTodos(
            "cjeongmin27@gmail.com",
            completion = { responseStatus, todos ->
                when (responseStatus) {
                    RESPONSE_STATUS.OKAY -> {
                        if (todos != null) {
                            okayCallback(todos)
                        }
                    }
                    RESPONSE_STATUS.FAIL -> {
                        failCallback()
                    }
                    RESPONSE_STATUS.NO_CONTENT -> {
                        noContentCallback()
                    }
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

        return todos.map {
            Section(it.key, it.value)
        }
    }

}