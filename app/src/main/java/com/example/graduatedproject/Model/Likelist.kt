package com.example.graduatedproject.Model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Likelist(
    //Gson을 사용할 때 클래스 필드에 @SerializedName 어노테이션을 붙여야 한다.
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("tagId")
    var tagId: Int = 0,

    @SerializedName("name")
    var name: String = ""
)
