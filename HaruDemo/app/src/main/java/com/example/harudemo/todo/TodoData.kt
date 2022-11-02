package com.example.harudemo.todo

import android.util.Log
import android.widget.Toast
import com.example.harudemo.retrofit.RetrofitManager
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
}