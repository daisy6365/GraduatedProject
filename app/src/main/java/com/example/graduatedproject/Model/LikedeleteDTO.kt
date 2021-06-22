package com.example.graduatedproject.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LikedeleteDTO {
    //보낼 (삭제될)관심주제 키워드
    @SerializedName("likeTopic")
    @Expose
    var likeTopicdelete : String = ""

}