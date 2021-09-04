package com.example.graduatedproject.Activity

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    init{
        instance = this
    }
    companion object {
        lateinit var instance: GlobalApplication
        fun ApplicationContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        //NATIVE App key 넣기
        //kakao SDK 초기화
        KakaoSdk.init(this, "6926e70a0f244bc29fdd1a01fe4997d5")
    }
}