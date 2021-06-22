package com.example.graduatedproject.Util

import com.example.graduatedproject.Model.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface InfoService {
    //로그인
    @POST("localhost:8000/auth-service/auth")
    abstract fun requestLogin(
            //로그인시 전달 값
            @Header("KakaoToken") kakaoToken: String
    ): Call<LoginDTO>
    // <> 안에 요청안에 데이터 or 응답에 대한 매핑

    //로그아웃
    @DELETE("localhost:8000/auth-service/auth")
    abstract fun requestLogout(
            //로그아웃시 전달 값
            @Header("Authorization") accessToken: String
    ): Call<LogoutDTO>

    //연결끊기(회원탈퇴)
    @DELETE("localhost:8000/user-service/users")
    abstract fun requestLogdelete(
            //회원탈퇴시 전달 값
            @Header("Authorization") accessToken: String
    ): Call<LogdeleteDTO>

    //관심주제리스트
    @GET("localhost:8000/user-service//users/tags")
    abstract fun requestLikelist(
            //관심주제리스트 조회시  전달 값
            @Header("Authorization") accessToken: String,
            @Body body: JsonObject
    ): Call<LikelistDTO>

    //관심주제검색
    @POST("localhost:8000/user-service/users/tags/2")
    abstract fun requestLikesearch(
            //관심주제 검색 시 전달 값
            @Header("Authorization") accessToken: String,
            @Body body: JsonObject
    ): Call<LikesearchDTO>

    //관심주제삭제
    @DELETE("localhost:8000/user-service/users/tags/2")
    abstract fun requestLikedelete(
            //관심주제삭제시 전달 값
            @Header("Authorization") accessToken: String,
            @Body body: JsonObject
    ): Call<LikedeleteDTO>

    //동네정보

    //친구목록

    //프로필정보


}