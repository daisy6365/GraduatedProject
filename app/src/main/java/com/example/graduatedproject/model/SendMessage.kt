package com.example.graduatedproject.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SendMessage (
    @SerializedName("message")
    @Expose
    var message : String? = null,

    @SerializedName("roomId")
    @Expose
    var studyChatId : Int? = null
)