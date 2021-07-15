package com.example.graduatedproject.Model

import com.google.gson.annotations.SerializedName

data class CategoryChild (
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("name")
    var name: String = ""
)