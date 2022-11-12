package com.example.harudemo.todo.types

data class TodoLog(
    val id: Number,
    val todoId: Number,
    val date: String,
    val completed: Boolean,
)