package com.example.harudemo.todo.types

// DB에 저장된 데이터를 표현하는 클래스
data class TodoDateInterface(
    val todoId: Int,
    val date: String,
    val completed: Boolean,
)