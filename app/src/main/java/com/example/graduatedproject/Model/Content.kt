package com.example.graduatedproject.Model

import com.google.gson.annotations.SerializedName


class Content {
    @SerializedName("content.[].id")
    var id: Int = 0

    @SerializedName("content.[].name")
    var name: String = ""
}