package com.example.graduatedproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.graduatedproject.Model.LikeSerch
import com.example.graduatedproject.Model.LikesearchDTO
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_liketopic_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiketopicSearchActivity : AppCompatActivity() {
    private val TAG = LiketopicSearchActivity::class.java.simpleName
    val PREFERENCE = "SharedPreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liketopic_search)

        val listSize = 6
        val searchList = ArrayList<LikeSerch>()


        // 관심주제 검색
        // androidx.appcompat.widget.SearchView에 텍스트 치고 검색 -> 리스트 형태로 검색결과 나옴
        // 검색결과는 6개까지만 보여주고 짜르기
        // 결과중 하나 누르면 화면에 추가됨 & 서버에 보냄
        liketopic_searchview.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //쿼리텍스트가 제출됐을때 반응

                //쿼리텍스트 서버로 보내기
                val paramObject = JsonObject()
                paramObject.addProperty("likeSearch", query)

                //검색결과 리스트 받기
                ServerUtil.retrofitService.requestLikesearch(paramObject)
                    .enqueue(object : Callback<LikesearchDTO> {
                        override fun onResponse(call: Call<LikesearchDTO>, response: Response<LikesearchDTO>) {
                            if (response.isSuccessful) {

                                //6개정도만 반복문 돌려서
                                // 서버로 부터 받은 검색결과를 List에 하나씩 넣음 & 하나씩 화면에 붙임
                                for(i in 0 .. listSize){
                                    searchList.add(LikeSerch(response.body()!!.get(0).likeSearches.toString()))
                                }

                                Log.d(TAG, "검색결과리스트 받기 성공")
                                // 받은 관심주제리스트 ListView에 item을 통해 화면에 출력
                                // 리스트 0~29까지 훝으면서 item에 값을 저장한뒤 ListView에 하나씩 붙이기

                                finish()
                            }
                        }

                        override fun onFailure(call: Call<LikesearchDTO>, t: Throwable) {
                            Log.d(TAG, "검색결과리스트 받기 실패")
                            Toast.makeText(this@LiketopicSearchActivity, "검색결과리스트 받기 실패", Toast.LENGTH_LONG).show()
                        }
                    })

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //쿼리텍스트가 변할때마다 반응
                return false
            }
        })
    }
}