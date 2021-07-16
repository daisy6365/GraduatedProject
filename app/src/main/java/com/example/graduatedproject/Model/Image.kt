package com.example.graduatedproject.Model

import com.google.gson.annotations.SerializedName

data class Image (
    @SerializedName("profileImage")
    var profileImage: String = "",

    @SerializedName("thumbnailImage")
    var thumbnailImage: String = ""
)