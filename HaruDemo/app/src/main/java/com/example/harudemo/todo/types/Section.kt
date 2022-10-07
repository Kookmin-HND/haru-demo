package com.example.harudemo.todo.types

// Recycler View에서 Section Title, 하위 Recycler View에서 표시할 todoList를 데이터로 가지고 있는 클래스
data class Section(
    val sectionTitle: String,
    var todoList: ArrayList<Todo>
)
