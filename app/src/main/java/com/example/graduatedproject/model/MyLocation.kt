package com.example.graduatedproject.model

import com.google.gson.annotations.SerializedName

data class MyLocation (
    @SerializedName("id")
    var id: Int = 1,

    @SerializedName("code")
    var code: String = "",

    @SerializedName("city")
    var city: String = "",

    @SerializedName("gu")
    var gu: String = "",

    @SerializedName("dong")
    var dong: String = "",

    @SerializedName("ri")
    var ri: String = "",

    //위도
    @SerializedName("let")
    var let: Double = 0.0,

    //경도
    @SerializedName("len")
    var len: Double = 0.0,

    @SerializedName("codeType")
    var codeType: String = ""
)




