package com.example.graduatedproject.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LikesearchDTO {
    //보낼 관심주제 검색 키워드
    @SerializedName("likeSearch")
    @Expose
    var likeSearch : String = ""

    //받을 관심주제 검색결과 리스트
    @SerializedName("likeSearchlist")
    @Expose
    val likeSearchlist : ArrayList<LikeSerch> = ArrayList()

    //보낼 관심주제 키워드
    @SerializedName("likeTopic")
    @Expose
    var likeTopic : String = ""

}
class LikeSerch{
        @SerializedName("likeSearch")
        var likeSearches : String = ""
}
