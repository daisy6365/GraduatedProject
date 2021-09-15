package com.example.graduatedproject.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChatRoom (
    @SerializedName("id")
    @Expose
    var id : Int = 0,

    @SerializedName("name")
    @Expose
    var name : String = "",

    @SerializedName("studyId")
    @Expose
    var studyId : Int = 0

)