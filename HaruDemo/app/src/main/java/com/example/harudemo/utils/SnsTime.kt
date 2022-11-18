package com.example.harudemo.utils

import java.text.SimpleDateFormat
import java.util.*

//데이터베이스에 저장된 createdAt을 바탕으로 현재 시간과 차이를 구하는 함수
fun getTimeDifference(preTime: String): String {
    val postDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(preTime)
    val diff = Date().time - postDate.time

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7

    if (weeks > 0) return (weeks.toString() + "주 전")
    if (days > 0) return (days.toString() + "일 전")
    if (hours > 0) return (hours.toString() + "시간 전")
    if (minutes > 0) return (minutes.toString() + "분 전")
    if (seconds > 0) return (seconds.toString() + "초 전")

    return diff.toString()
}