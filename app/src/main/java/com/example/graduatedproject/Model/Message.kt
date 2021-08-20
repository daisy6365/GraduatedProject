package com.example.graduatedproject.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Message (
    @SerializedName("sender")
    @Expose
    var sender : String? = null,

    @SerializedName("message")
    @Expose
    var message : String? = null,

    @SerializedName("createdAt")
    @Expose
    var createdAt : String? = null

)