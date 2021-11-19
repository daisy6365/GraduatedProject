package com.example.graduatedproject.model

import com.google.gson.annotations.SerializedName

data class Exception (
    @SerializedName("message")
    var message: String? = null,

    @SerializedName("attributes")
    var attributes: Attribute? = Attribute()
)

data class Attribute (
    @SerializedName("locationCode")
    var locationCode: String? = null
)