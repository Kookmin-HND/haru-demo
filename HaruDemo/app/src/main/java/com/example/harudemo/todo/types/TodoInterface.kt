package com.example.harudemo.todo.types

data class TodoInterface(
    val todoId: Int,
    val writer: String,
    val folder: String,
    val content: String,
    val createdAt: String,
    val begin: String,
    val end: String,
)
