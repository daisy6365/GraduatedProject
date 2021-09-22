package com.example.graduatedproject.model

import com.google.gson.annotations.SerializedName


data class ContentTag(
    //태그 (주제) ID
    @SerializedName("id")
    var id: Int = 0,

    //태그 (주제) 이름
    @SerializedName("name")
    var name: String = ""
)

