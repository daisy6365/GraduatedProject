package com.example.graduatedproject.Activity

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //NATIVE App key 넣기
        KakaoSdk.init(this, "6926e70a0f244bc29fdd1a01fe4997d5")
    }
}