package com.example.harudemo.model

//SnsPost에 대한 데이터 모델
data class SnsPost(
    var writer: String?,
    var content: String?,
    var createdAt: String?,
    var writerPhoto: String?,
) {
}