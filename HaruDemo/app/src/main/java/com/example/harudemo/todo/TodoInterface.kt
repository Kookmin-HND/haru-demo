package com.example.harudemo.todo

data class TodoInterface(
    val todoId: Int,
    val writer: String,
    val folder: String,
    val content: String,
    val createdAt: String,
)
