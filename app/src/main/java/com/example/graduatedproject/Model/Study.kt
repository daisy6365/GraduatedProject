package com.example.graduatedproject.Model

import com.google.gson.annotations.SerializedName

data class Study (
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("name")
    var name: String = "",

    @SerializedName("numberOfPeople")
    var numberOfPeople: Int = 0,

    @SerializedName("currentNumberOfPeople")
    var currentNumberOfPeople: Int = 0,

    @SerializedName("content")
    var content: String = "",

    @SerializedName("online")
    var online: Boolean? = null,

    @SerializedName("offline")
    var offline: Boolean? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("image")
    var image: Image? = Image(),

    @SerializedName("location")
    var location: ContentLocation? = ContentLocation(),

    @SerializedName("parentCategory")
    var parentCategory: Category? = Category(),

    @SerializedName("childCategory")
    var childCategory: Category? = Category(),

    @SerializedName("studyTags")
    var studyTags: ArrayList<String>? = arrayListOf()
)
