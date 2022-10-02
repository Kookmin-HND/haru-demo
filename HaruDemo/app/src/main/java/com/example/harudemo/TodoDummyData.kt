package com.example.harudemo

import android.util.Log
import com.example.harudemo.todo.Section
import com.example.harudemo.todo.Todo
import com.example.harudemo.todo.TodoInterface
import com.example.harudemo.todo.TodoDateInterface

object TodoDummyData {
    val todoInterfaceTables = arrayListOf<TodoInterface>(
        TodoInterface(
            todoId = 1,
            writer = "tester@example.com",
            folder = "금융",
            content = "친구비 내기",
            createdAt = "2022.9.30"
        ),
        TodoInterface(
            todoId = 2,
            writer = "tester@example.com",
            folder = "금융",
            content = "월세 내기",
            createdAt = "2022.9.30"
        ),
        TodoInterface(
            todoId = 3,
            writer = "tester@example.com",
            folder = "과제",
            content = "모바일 프로그래밍",
            createdAt = "2022.9.30"
        )
    )

    val todoDateInterfaceTables = arrayListOf<TodoDateInterface>(
        TodoDateInterface(
            todoId = 1,
            date = "2022.9.30",
            completed = false,
        ),
        TodoDateInterface(
            todoId = 1,
            date = "2022.10.1",
            completed = false,
        ),
        TodoDateInterface(
            todoId = 1,
            date = "2022.10.5",
            completed = false,
        ),
        TodoDateInterface(
            todoId = 2,
            date = "2022.10.1",
            completed = false,
        ),
        TodoDateInterface(
            todoId = 3,
            date = "2022.10.2",
            completed = false
        )
    )
}