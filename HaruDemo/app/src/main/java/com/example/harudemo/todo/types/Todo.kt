package com.example.harudemo.todo.types

import java.io.Serializable

// TodoData Interface
data class Todo(
    var id: Number,
    var writer: String,
    var folder: String,
    var content: String,
    var date: String,
    var completed: Boolean,
) : Serializable
