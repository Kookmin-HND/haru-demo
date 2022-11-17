package com.example.harudemo

import android.app.Application
import com.example.harudemo.model.UserInfo

/*
자유롭게 컨텍스트를 불러오기 위한 클래스
어디서든 컨텍스트를 호출할 때 App.instance를 치면 편하게 호출이 가능하다.
 */
class App : Application(){
    companion object{
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

    }
}
