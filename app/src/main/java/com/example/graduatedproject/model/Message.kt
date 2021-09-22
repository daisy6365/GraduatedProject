package com.example.graduatedproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Message (
    @SerializedName("userId")
    @Expose
    var userId : Int? = null,

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