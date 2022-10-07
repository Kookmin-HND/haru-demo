package com.example.harudemo.todo.types

// DB에 있는 데이터를 표현한 클래스
data class TodoInterface(
    val todoId: Int,
    val writer: String,
    val folder: String,
    val content: String,
    val createdAt: String,
    val begin: String,
    val end: String,
)
