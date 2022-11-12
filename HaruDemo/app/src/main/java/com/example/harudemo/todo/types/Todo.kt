package com.example.harudemo.todo.types

import java.io.Serializable

// TodoData Interface
data class Todo(
    val id: Number,
    val writer: String,
    val folder: String,
    val content: String,
) : Serializable
