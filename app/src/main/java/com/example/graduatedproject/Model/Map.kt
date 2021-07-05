package com.example.graduatedproject.Model

import com.google.gson.annotations.SerializedName

class Map {
    @SerializedName("/locations/{locationId}")
    var locationId: String = ""
}