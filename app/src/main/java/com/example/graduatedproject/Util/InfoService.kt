package com.example.graduatedproject.Util

import com.example.graduatedproject.BuildConfig
import com.example.graduatedproject.Model.*
import com.google.gson.JsonObject
import com.kakao.kakaotalk.StringSet.token
import retrofit2.Call
import retrofit2.http.*

interface InfoService {

    //로그인
    @POST("/auth-service/auth")
    abstract fun requestLogin(
            //로그인시 전달 값
            @Header("kakaoToken")  kakaoToken: String
    ): Call<Void>
    // <> 안에 요청안에 데이터 or 응답에 대한 매핑

    //로그아웃
    @DELETE("/auth-service/auth")
    abstract fun requestLogout(
            //로그아웃시 전달 값
            @Header("Authorization") accessToken: String
    ): Call<Void>

    //연결끊기(회원탈퇴)
    @DELETE("/user-service/users")
    abstract fun requestLogdelete(
            //회원탈퇴시 전달 값
            @Header("Authorization") accessToken: String
    ): Call<Void>

    //관심주제리스트
    @GET("/user-service//users/tags")
    abstract fun requestLikelist(
            //관심주제리스트 조회시 전달 값
            @Header("Authorization") accessToken: String
    ): Call<LikelistDTO>

    //관심주제검색
    @POST("/user-service/users/tags/2")
    abstract fun requestLikesearch(
            //관심주제 검색 시 전달 값
            @Body body: JsonObject
    ): Call<LikesearchDTO>

    //관심주제추가
    @POST("/user-service/users/tags/2")
    abstract fun requestLikeadd(
        //관심주제 검색 시 전달 값
        @Header("Authorization") accessToken: String,
        @Body body: JsonObject
    ): Call<LikesearchDTO>

    //관심주제삭제
    @DELETE("/user-service/users/tags/2")
    abstract fun requestLikedelete(
            //관심주제삭제시 전달 값
            @Header("Authorization") accessToken: String,
            @Body body: JsonObject
    ): Call<Void>

    //동네정보조회
    @POST("/user-service/users/tags/2")
    abstract fun requestLocation(
        //관심주제 검색 시 전달 값
        @Header("Authorization") accessToken: String
    ): Call<Void>

    //친구목록

    //프로필정보


}