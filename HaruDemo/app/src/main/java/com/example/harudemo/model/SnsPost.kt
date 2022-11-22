package com.example.harudemo.model

//SnsPost에 대한 데이터 모델
data class SnsPost(
    var id: Int,
    var writer: String?,
    var category: String?,
    var content: String?,
    var createdAt: String?,
    var updatedAt: String?,
    var writerPhoto: String?,
    var average: Int?,
    var commentNumber: Int,
    var postLikeList: ArrayList<String>,
    var postImageList: ArrayList<String>,
) {
}