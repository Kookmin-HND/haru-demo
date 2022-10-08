package com.example.harudemo.utils

// 문자열이 제이슨 형태인지, 제이슨 배열 형태인지 확인하는 함수
fun String?.isJsonObject(): Boolean {
    return this?.startsWith("{") == true && this.endsWith("}");
}

fun String?.isJsonArray(): Boolean{
    return this?.startsWith("[") == true && this.endsWith("]");
}
