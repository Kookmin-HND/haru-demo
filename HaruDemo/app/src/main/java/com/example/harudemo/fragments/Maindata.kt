package com.example.harudemo.fragments

class maindata {
    //모든 레이아웃에서 공유할 수 있는 메인데이터
    companion object {
        var wom : Boolean = false
        var contents = Array(30){Array(13){Array(32){""} }}
        var successrate = Array(30){Array(13){Array(32){0} }}
        var week_content = 0
    }
}