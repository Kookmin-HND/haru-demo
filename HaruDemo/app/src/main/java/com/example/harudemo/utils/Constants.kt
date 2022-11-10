package com.example.harudemo.utils

object Constants {
    const val TAG: String = "로그"
}

//API 호출 결과 enum
enum class RESPONSE_STATUS {
    OKAY, FAIL, NO_CONTENT
}
//10.0.0.2
object API {
    const val BASE_URL: String = "http://192.168.0.48:8000/api/"
    const val API_KEY: String = ""
    const val POSTS: String = "posts"
    const val TODOS: String = "todos"
    const val USERS : String = "users"
}