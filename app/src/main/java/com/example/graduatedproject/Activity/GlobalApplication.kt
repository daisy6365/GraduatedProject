package com.example.graduatedproject.Activity

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //NATIVE App key 넣기
        KakaoSdk.init(this, "876f90163f139441098f9d9ff3a97ee6")
    }
}