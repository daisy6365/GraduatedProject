package com.example.graduatedproject.Model

import com.google.gson.annotations.SerializedName

data class Group (
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("studyId")
    var studyId: Int = 0,

    @SerializedName("gatheringTime")
    var gatheringTime: String? = null,

    @SerializedName("numberOfPeople")
    var numberOfPeople: Int = 0,

    @SerializedName("shape")
    var shape: String? = null,

    @SerializedName("content")
    var content: String? = null,

    @SerializedName("place")
    var place: Place? = Place(),

    @SerializedName("apply")
    var apply: Boolean? = null
)

data class Place(
    @SerializedName("name")
    var name: String? = null,

    @SerializedName("let")
    var let: Double = 0.0,

    @SerializedName("len")
    var len: Double = 0.0,
)