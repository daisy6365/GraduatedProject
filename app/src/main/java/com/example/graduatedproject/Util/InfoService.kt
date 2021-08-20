package com.example.graduatedproject.Util

import com.example.graduatedproject.Model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface InfoService {
    var i : String

    //로그인
    @POST("/auth-service/auth")
    abstract fun requestLogin(
            //로그인시 전달 값
            @Header("kakaoToken")  kakaoToken: String
    ): Call<Void>
    // <> 안에 요청안에 데이터 or 응답에 대한 매핑

    //로그아웃
    @DELETE("/auth-service/auth")
    abstract fun requestLogout(
            //로그아웃시 전달 값
            @Header("Authorization") accessToken: String
    ): Call<Void>

    //연결끊기(회원탈퇴)
    @DELETE("/user-service/users")
    abstract fun requestLogdelete(
            //회원탈퇴시 전달 값
            @Header("Authorization") accessToken: String
    ): Call<Void>




    //관심주제리스트조회
    @GET("/user-service/users/tags")
    abstract fun requestLikelist(
            //관심주제리스트 조회시 전달 값
            @Header("Authorization") accessToken: String
    ): Call<ArrayList<Likelist>>

    //관심주제삭제
    @DELETE("/user-service/users/tags/{tagId}")
    abstract fun requestLikeDelete(
            //관심주제삭제시 전달 값
            @Header("Authorization") accessToken: String,
            @Path("tagId") tagId : Int
    ): Call<Void>

    //관심주제검색리스트(페이징)
    @GET("/study-service/tags")
    abstract fun requestLikesearch(
        //관심주제 검색 시 전달 값
        @Query("page") page : Int,
        @Query("size") size : Int,
        @Query("name") name : String
    ): Call<Likesearch>

    //관심주제추가
    @POST("/user-service/users/tags/{tagId}")
    abstract fun requestLikeadd(
        //관심주제 검색 시 전달 값
        @Header("Authorization") accessToken: String,
        @Path("tagId") tagId : Int
    ): Call<Void>




    //지역정보 검색
    @GET("/location-service/locations/search")
    abstract fun requestLocationsearch(
        @Header("Authorization") accessToken: String,
        @Query("page") page : Int,
        @Query("size") size : Int,
        @Query("searchName") searchName : String
    ): Call<LocationSearch>

    //지역정보 추가, 저장
    @PATCH("/user-service/users/locations/{locationId}")
    abstract fun requestLocationmodify(
        @Header("Authorization") accessToken: String,
        @Path("locationId") LocationId : Int
    ): Call<Profile>

    //지역정보 ID 조회
    @GET("/location-service/locations/{locationId}")
    abstract fun requestLocation(
        @Header("Authorization") accessToken: String,
        @Path("locationId") locationId : Int
    ) : Call<MyLocation>

    //현재위치 행정코드 받기 with kakaoretrofit
    @GET("/v2/local/geo/coord2regioncode.json")
    abstract fun requestCode(
        @Header("Authorization") kakaoToken: String,
        @Query("x") longitudeX : Double,
        @Query("y") latitudeY : Double
    ): Call<MapCode>


    @GET("/location-service/locations/code")
    abstract fun requestLocationId(
        @Header("Authorization") accessToken: String,
        @Query("code") code : String
    ): Call<MyLocation>



    //사용자 정보 (이름, 프로필사진, 지역정보)
    @GET("/user-service/users/profile")
    abstract fun requestProfile(
        //사용자조회시 전달값
        @Header("Authorization") accessToken: String
    ): Call<Profile>

    //프로필수정
    @Multipart
    @PATCH("/user-service/users/profile")
    abstract fun requestModifyProfile(
        //프로필수정시 전달값
        @Header("Authorization") accessToken: String,
        @Part imageFile : MultipartBody.Part?,

        //deleteImage,nickName
        @Part("request") requestBody: RequestBody
    ): Call<Void>

    //친구목록


    //마이스터디조회
    @GET("/study-service/users/studies")
    abstract fun requestMyStudy(
        @Header("Authorization") accessToken: String
    ): Call<ArrayList<Study>>



    //스터디생성
    @Multipart
    @POST("/study-service/studies")
    abstract fun SendCreateStudyInfo(
        @Header("Authorization") accessToken: String,
        @Part imageFile : MultipartBody.Part?,

        //name,numberOfPeople,content,tags
        //online,offline,locationCode,categoryId
        @Part("request") requestBody: RequestBody
    ): Call<Study>

    //부모카테고리
    @GET("/study-service/categories/parent")
    abstract fun requestCategoryParent():Call<ArrayList<Category>>

    //자식카테고리
    @GET("/study-service/categories/{i}/child")
    abstract fun requestCategoryChild(
        @Path("i") i : Int
    ):Call<ArrayList<Category>>

    //스터디상세조회
    @GET("/study-service/studies/{studyId}")
    abstract fun requestStudy(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int
    ):Call<Study>

    //스터디 검색(페이징)
    @GET("/study-service/studies")
    abstract fun requestStudySearch(
        @Header("Authorization") accessToken: String,
        @Query("page") page : Int,
        @Query("size") size : Int,
        @Query("offline") offline : Boolean,
        @Query("online") online : Boolean,
        @Query("searchKeyword") searchKeyword : String,
        @Query("categoryId") categoryId : Int,
    ):Call<StudySearch>

    //스터디 참가 신청
    @POST("/study-service/studies/{studyId}/waitUsers")
    abstract fun requestStudyApply(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int
    ):Call<Void>

    //스터디 참가신청 취소
    @DELETE("/study-service/studies/{studyId}/waitUsers")
    abstract fun requestStudyApplyCancle(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int
    ):Call<Void>




    //스터디 탈퇴
    @DELETE("/study-service/studies/{studyId}/users")
    abstract fun requestStudyDelete(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int
    ):Call<Void>

    //멤버 조회
    @GET("/study-service/studies/{studyId}/users")
    abstract fun requestStudyMember(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int
    ):Call<ArrayList<Profile>>


    //멤버 삭제
    @DELETE("/study-service/studies/{studyId}/users/{userId}")
    abstract fun requestMemberDelete(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int,
        @Path("userId") userId : Int
    ):Call<Void>

    //지원 멤버 조회
    @GET("/study-service/studies/{studyId}/waitUsers")
    abstract fun requestStudyAddMember(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int
    ):Call<ArrayList<Profile>>

    //지원 멤버 삭제
    @DELETE("/study-service/studies/{studyId}/waitUsers/{userId}")
    abstract fun requestAddMemberDelete(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int,
        @Path("userId") userId : Int
    ):Call<Void>

    //멤버 추가
    @POST("/study-service/studies/{studyId}/users/{userId}")
    abstract fun requestAddMember(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int,
        @Path("userId") userId : Int
    ):Call<Void>

    //스터디방 수정
    @PATCH("/study-service/studies/{studyId}")
    abstract fun requestModifyStudy(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int,
        @Part imageFile : MultipartBody.Part?,

        //name,numberOfPeople,content,tags
        //online,offline,locationCode,categoryId
        //deleteImage,close
        @Part("request") requestBody: RequestBody
    ):Call<Study>

    //스터디방 삭제
    @DELETE("/study-service/studies/{studyId}")
    abstract fun requestDeleteStudy(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int
    ):Call<Void>




    //채팅방생성
    @POST("/chat-service/studies/{studyId}/chatRooms")
    abstract fun requestCreateChat(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int,
        @Path("name") name : String
    ):Call<ChatRoom>

    //채팅방 수정
    @PATCH("/chat-service/chatRooms/{chatRoomId}")
    abstract fun requestModifyChat(
        @Header("Authorization") accessToken: String,
        @Path("chatRoomId") chatRoomId : Int,
        @Path("name") name : String
    ):Call<ChatRoom>

    //채팅방 삭제
    @DELETE("/chat-service/chatRooms/{chatRoomId}")
    abstract fun requestDeleteChat(
        @Header("Authorization") accessToken: String,
        @Path("chatRoomId") chatRoomId : Int
    ):Call<Void>

    //채팅방 리스트 조회
    @GET("/chat-service/studies/{studyId}/chatRooms")
    abstract fun requestChatRoom(
        @Header("Authorization") accessToken: String,
        @Path("studyId") studyId : Int
    ):Call<ArrayList<ChatRoom>>

    //채팅 메세지 조회
    @GET("/chat-service/chatRooms/{chatRoomId}/chatMessages")
    abstract fun requestChat(
        @Header("Authorization") accessToken: String,
        @Query("page") page : Int,
        @Query("size") size : Int,
        @Query("chatRoomId") searchName : String
    ):Call<Chat>

}