package com.example.graduatedproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GroupList (
    @SerializedName("content")
    @Expose
    var content: ArrayList<Group>,

    //마지막 페이지 여부!!!
    @SerializedName("last")
    var last : Boolean? = null,

    //총 페이지 수
    @SerializedName("totalPages")
    var totalPages: Int = 0,

    //총 결과 수!!!!
    @SerializedName("totalElements")
    var totalElements: Int = 0,

    //페이지 크기
    @SerializedName("size")
    var size: Int = 0,

    //페이지 번호
    @SerializedName("number")
    var number: Int = 0,

    //현재 페이지 크기
    @SerializedName("numberOfElements")
    var numberOfElements: Int = 0,

    //처음 페이지 여부
    @SerializedName("first")
    var first:Boolean = true,

    //값이 비 었는지 여부
    @SerializedName("empty")
    var empty: Boolean = true
)