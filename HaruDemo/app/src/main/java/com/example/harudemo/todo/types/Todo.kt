package com.example.harudemo.todo.types

// 단순히 DB에 있는 두 데이터를 합친 데이터
data class Todo(
    val todo: TodoInterface,
    val todoDate: TodoDateInterface,
)
