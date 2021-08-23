package com.example.graduatedproject.Model

import android.os.Parcelable
import android.provider.ContactsContract
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Profile(
    //받을 사용자의 프로필 정보
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("kakaoId")
    var kakaoId: Int = 0,

    @SerializedName("userId")
    var userId: Int = 0,

    @SerializedName("nickName")
    var nickName: String = "",

    @SerializedName("gender")
    var gender: String = "",

    @SerializedName("ageRange")
    var ageRange: String = "",

    @SerializedName("role")
    var role: String? = null,

    @SerializedName("numberOfStudyApply")
    var numberOfStudyApply: Int = 0,

    @SerializedName("locationId")
    var locationId: Int = 0,

    @SerializedName("image")
    var image: Image = Image(),

    @SerializedName("register")
    var register: Boolean? = null


)



