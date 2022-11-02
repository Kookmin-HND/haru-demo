package com.example.harudemo.todo.types

// Todo Interface
data class Todo(
    val id: Number,
    val writer: String,
    val folder: String,
    val content: String,
    val date: String,
    val completed: Boolean,
)
