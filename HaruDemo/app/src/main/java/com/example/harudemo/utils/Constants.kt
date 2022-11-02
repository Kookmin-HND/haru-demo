package com.example.harudemo.utils

object Constants {
    const val TAG: String = "로그"
}

//API 호출 결과 enum
enum class RESPONSE_STATUS {
    OKAY, FAIL, NO_CONTENT
}

//테스트용 API
object API {
    const val BASE_URL: String = "https://jsonplaceholder.typicode.com/"
    const val API_KEY: String = ""
    const val POSTS: String = "posts"
    const val TODOS: String = "todos"
}