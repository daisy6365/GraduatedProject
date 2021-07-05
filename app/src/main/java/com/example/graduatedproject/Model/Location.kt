package com.example.graduatedproject.Model

import com.google.gson.annotations.SerializedName

class Location (
    @SerializedName("id")
    var id: Int = 0,

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
    var let: Int = 0,

    //경도
    @SerializedName("len")
    var len: Int = 0,

    @SerializedName("codeType")
    var codeType: String = ""
)