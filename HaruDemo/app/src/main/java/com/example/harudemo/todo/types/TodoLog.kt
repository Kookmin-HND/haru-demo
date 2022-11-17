package com.example.harudemo.todo.types

import java.io.Serializable

data class TodoLog(
    val id: Number,
    val todoId: Number,
    val date: String,
    val completed: Boolean,
) : Serializable