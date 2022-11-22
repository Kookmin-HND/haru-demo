package com.example.harudemo.model

//SnsPost에 대한 데이터 모델
data class SnsPost(
    var id: Int,
    var writer: String?,
    var content: String?,
    var createdAt: String?,
    var updatedAt: String?,
    var writerPhoto: String?,
<<<<<<< HEAD
    var average: Int?,
=======
    var commentNumber: Int,
    var postImageList : ArrayList<String>,
    var average: String?,
>>>>>>> d53d0086597f385ec9d2da6109fcd63c901c1a37
) {
}