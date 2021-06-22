package com.example.graduatedproject.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LikelistDTO {
    //보낼 사용자의 액세스토큰
    @SerializedName("accessToken")
    @Expose
    var accessToken : String = ""

    //받을 사용자의 관심주제 리스트
    @SerializedName("likeTopiclist")
    @Expose
    var liketopicList : ArrayList<TopicList> = ArrayList()


}
class TopicList{
    //관심주제들
    @SerializedName("likeTopics")
    var likeTopics : String = ""
}