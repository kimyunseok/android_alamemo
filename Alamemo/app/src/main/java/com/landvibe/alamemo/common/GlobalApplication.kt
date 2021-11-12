package com.landvibe.alamemo.common

import android.app.Application

/*
안드로이드 기본 어플 state를 관리하는 객체.
Manifest <application> 태그 안에 name에 클래스를지정해서 사용할 수 있다.
App의 instatnce를 선언함으로서 AppDatabase에서 companion object와 연계가능.
 */

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance : GlobalApplication
            private set //
    }
}