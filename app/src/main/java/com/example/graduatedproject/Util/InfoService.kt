package com.example.graduatedproject.Util

import com.example.graduatedproject.Model.LogdeleteDTO
import com.example.graduatedproject.Model.LoginDTO
import com.example.graduatedproject.Model.LogoutDTO
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface InfoService {
    //로그인
    @Headers("Name:kakaoToken", "Description:카카오 토큰")
    @POST("localhost:8000/auth-service/auth")
    abstract fun requestLogin(
        //로그인시 전달 값
        @Body body : JsonObject
    ): Call<LoginDTO>
    // <> 안에 요청안에 데이터 or 응답에 대한 매핑

    //로그아웃
    @Headers("Name:kakaoToken", "Description:카카오 토큰")
    @POST("localhost:8000/auth-service/auth")
    abstract fun requestLogout(
        //로그아웃시 전달 값
        @Body body : JsonObject
    ): Call<LogoutDTO>

    //연결끊기(회원탈퇴)
    @Headers("Name:kakaoToken", "Description:카카오 토큰")
    @POST("localhost:8000/auth-service/auth")
    abstract fun requestLogdelete(
        //회원탈퇴시 전달 값
        @Body body : JsonObject
    ): Call<LogdeleteDTO>

    //관심주제



    //동네정보

    //친구목록

    //프로필정보





}