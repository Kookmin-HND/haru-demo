package com.example.harudemo.model

//SnsPost에 대한 데이터 모델
data class SnsComment(
    var id: Int,
    var postId: Int,
    var writer: String?,
    var parentCommentId: Int,
    var content: String?,
    var createdAt: String?,
    var updatedAt: String?,
    var writerPhoto: String?,
) {
}