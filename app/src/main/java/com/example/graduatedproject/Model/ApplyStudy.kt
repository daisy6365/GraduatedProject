package com.example.graduatedproject.Model

import com.google.gson.annotations.SerializedName

data class ApplyStudy (
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("studyId")
    var studyId: Int? = null,

    @SerializedName("studyName")
    var studyName: String? = null,

    @SerializedName("status")
    var status: String? = null
)