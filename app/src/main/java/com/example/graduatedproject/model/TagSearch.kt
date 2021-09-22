package com.example.graduatedproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Likesearch (
    //보낼 관심주제 검색 키워드
    @SerializedName("content")
    @Expose
    var content: ArrayList<ContentTag> = ArrayList(),

    //받을 관심주제 검색결과 리스트
    @Expose
    val pageable: PageableTag = PageableTag(),

    //마지막 페이지 여부!!!
    @SerializedName("last")
    var last : Boolean = false,

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

    @Expose
    var sort: Sort = Sort(),

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



data class PageableTag (
    var sort : Sort = Sort(),

    //페이지 크기
    @SerializedName("offset")
    var offset: Int = 0,

    //현재 페이지 번호
    @SerializedName("pageNumber")
    var pageNumber: Int = 0,

    //페이지 크기
    @SerializedName("pageSize")
    var pageSize: Int = 0,

    //페이징 여부
    @SerializedName("paged")
    var paged: Boolean = true,

    //비 페이징 여부
    @SerializedName("unpaged")
    var unpaged: Boolean = true
)
