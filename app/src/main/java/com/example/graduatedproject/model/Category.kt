package com.example.graduatedproject.model

import com.google.gson.annotations.SerializedName

data class Category (
        @SerializedName("id")
        var id: Int = 0,

        @SerializedName("name")
        var name: String = ""
)