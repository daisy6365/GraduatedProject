package com.example.graduatedproject.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginDTO {
    @SerializedName("kakaoToken")
    @Expose
    val kakaoToken : String = ""
}