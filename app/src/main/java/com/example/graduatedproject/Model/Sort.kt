package com.example.graduatedproject.Model

import com.google.gson.annotations.SerializedName

class Sort {
    @SerializedName("sorted")
    var sorted: Boolean = true

    @SerializedName("unsorted")
    var unsorted: Boolean = true

    @SerializedName("empty")
    var empty: Boolean = true

}