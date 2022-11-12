package com.example.harudemo.utils

object Constants {
    const val TAG: String = "로그"
}

//API 호출 결과 enum
enum class RESPONSE_STATUS {
    OKAY, FAIL, NO_CONTENT
}

object API {
<<<<<<< HEAD
    //const val BASE_URL: String = "http://192.168.0.7:8000/api/"

=======
>>>>>>> c35a841864c29f1b4d5cc84793ef31eaa754a999
    const val BASE_URL: String = "http://10.0.2.2:8000/api/"
    const val API_KEY: String = ""
    const val RECENT_POSTS: String = "posts/recent/{postId}"
    const val POSTS: String = "posts"
    const val COMMENTS: String = "comments"
    const val TODOS: String = "todos"
}