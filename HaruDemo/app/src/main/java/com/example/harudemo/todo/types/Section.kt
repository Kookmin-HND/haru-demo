package com.example.harudemo.todo.types

data class Section(
    val sectionTitle: String,
    var todoList: ArrayList<Todo>
)
