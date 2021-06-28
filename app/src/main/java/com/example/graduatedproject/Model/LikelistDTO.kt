package com.example.graduatedproject.Model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class LikelistDTO (
    //받을 사용자의 관심주제 리스트
    @SerializedName("likeTopiclist")
    @Expose
    var liketopicList : ArrayList<TopicList>
)
@Parcelize
data class TopicList (
    @SerializedName("id")
    var id : Int = 0,

    @SerializedName("tagId")
    var tagId : Int = 0,

    @SerializedName("name")
    var name : String = ""
) : Parcelable

