package com.example.graduatedproject.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LikesearchDTO {
    //보낼 관심주제 검색 키워드
    @SerializedName("name")
    @Expose
    var name : String = ""

    //받을 관심주제 검색결과 리스트
    @SerializedName("page")
    @Expose
    val page : ArrayList<LikeSerch> = ArrayList()

    //보낼 관심주제 키워드
    @SerializedName("size")
    @Expose
    var size : String = ""

}
class LikeSerch{
        @SerializedName("likeSearch")
        var likeSearches : String = ""
}
