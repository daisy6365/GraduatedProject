package com.example.graduatedproject.Util

import android.util.Log
import com.kakao.kakaotalk.StringSet.token
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServerUtil {
    private val TAG = this::class.java.simpleName
    var retrofitService: InfoService

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
//        val request = Request
//            .Builder()
//            .addHeader("Authorization", "Bearer $token")
//            .build()
//        //Okhttp : 서버와 http,http/2 프로토콜 통신을 위한 client library

        val logger = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .build()

        //Retrofit : type-safe한 HTTP 클라이언트 라이브러리
        //retrofit은 Okhttp 클라이언트를 디폴트로 선언
        //다른 HTTP 모듈보다 빠름
        val retrofit = Retrofit
            .Builder()
            .baseUrl("http://localhost:8000")
            //Gson은 Json문서를 받아서 자동으로 java class 형태로 만들어주는 역할
            .addConverterFactory(GsonConverterFactory.create())
            .client(logger)
            .build()

        retrofitService = retrofit.create(InfoService::class.java)
        Log.d(TAG, "서버 연결")

    }

}