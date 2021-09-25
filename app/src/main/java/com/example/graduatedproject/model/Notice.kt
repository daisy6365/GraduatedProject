package com.example.graduatedproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Notice (
    //알림id
    @SerializedName("id")
    @Expose
    var id: Int,

    //알림 회원 id
    @SerializedName("userId")
    @Expose
    var userId: Int,

    //알림 제목
    @SerializedName("title")
    @Expose
    var title: String,

    //알림 내용
    @SerializedName("content")
    @Expose
    var content: String,

    //알림 생성 시간
    @SerializedName("createdAt")
    @Expose
    var createdAt: String
    )