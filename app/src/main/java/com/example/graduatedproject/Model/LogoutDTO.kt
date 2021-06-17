package com.example.graduatedproject.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LogoutDTO {
    @SerializedName("accessToken")
    @Expose
    var accessToken : String = ""

}