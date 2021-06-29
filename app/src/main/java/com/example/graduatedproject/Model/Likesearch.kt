package com.example.graduatedproject.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Likesearch {
    //보낼 관심주제 검색 키워드
    @SerializedName("content")
    @Expose
    var content: ArrayList<Content> = ArrayList()

    //받을 관심주제 검색결과 리스트
    @Expose
    val pageable: Pageable = Pageable()

    @SerializedName("last")
    var last:Boolean = true

    @SerializedName("totalPages")
    var totalPages: Int = 0

    @SerializedName("totalElementsd")
    var totalElements: Int = 0

    @SerializedName("size")
    var size: Int = 0

    @SerializedName("numberd")
    var number: Int = 0

    //보낼 관심주제 키워드
    @Expose
    var sort: Sort = Sort()

    @SerializedName("numberOfElements")
    var numberOfElements: Int = 0

    @SerializedName("first")
    var first:Boolean = true

    @SerializedName("empty")
    var empty: Boolean = true
}

class Pageable {
    var sort : Sort = Sort()

    @SerializedName("offset")
    var offset: Int = 0

    @SerializedName("pageNumber")
    var pageNumber: Int = 0

    @SerializedName("pageSize")
    var pageSize: Int = 0

    @SerializedName("paged")
    var paged: Boolean = true

    @SerializedName("unpaged")
    var unpaged: Boolean = true

}