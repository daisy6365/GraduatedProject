package com.example.graduatedproject.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.graduatedproject.Model.LikelistDTO
import com.example.graduatedproject.Model.TopicList
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.android.material.chip.Chip
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_liketopic.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiketopicActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.java.simpleName
    val PREFERENCE = "SharedPreference"

    var likeListDTO :LikelistDTO = LikelistDTO()
    val liketopicList :ArrayList<TopicList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liketopic)


        //서버에 보내야 할것 : 액세스토큰, 입력값
        //받아야 할것 : 관심주제검색어, 관심주제리스트
        //저장된 엑세스 토큰을 가져옴
        var pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        var accessToken: String = pref.getString("access_token", "").toString()


        //엑세스토큰 서버에 보냄 -> 관심주제 리스트 받아오기 위해서
        val paramObject = JsonObject()
        paramObject.addProperty("accessToken", accessToken)

        ServerUtil.retrofitService.requestLikelist(paramObject)
                .enqueue(object : Callback<LikelistDTO> {
                    override fun onResponse(call: Call<LikelistDTO>, response: Response<LikelistDTO>) {
                        if (response.isSuccessful) {
                            likeListDTO = response.body()!!
                            for (i in 0 until likeListDTO.liketopicList.size) {
                                //리스트에 받기
                                liketopicList.add(TopicList.get(i).likeTopics)

                                //chipitem에 liketopicList에 담기
                                val chip = Chip(this)
                                chip.text =
                                liketopic_chipgroup.addView(chip)

                                Log.d(TAG, "관심주제리스트 받기 성공")
                                // 받은 관심주제리스트 ListView에 item을 통해 화면에 출력
                                // 리스트 0~29까지 훝으면서 item에 값을 저장한뒤 ListView에 하나씩 붙이기

                                finish()
                            }
                            // 관심주제 삭제
                            // X버튼 누르면 -> LikedeleteDTO를 통해 키워드 서버로 보내기 -> 서버는 사용자의 관심리스트에서 삭제함
                            // 해당 키워드 item 화면에 없애기

                        }
                    }


                    override fun onFailure(call: Call<LikelistDTO>, t: Throwable) {
                        Log.d(TAG, "로그인 실패")
                        Toast.makeText(this@LiketopicActivity, "관심주제리스트 받기 실패", Toast.LENGTH_LONG).show()
                    }
                })



        // 관심주제 추가 -> 검색
        liketopicplus_chip.setOnClickListener {
            val intent = Intent(this@LiketopicActivity, LiketopicSearchActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

            //키워드를 주고 화면전환이 되면
            //화면에 키워드 추가
        }




    }

}