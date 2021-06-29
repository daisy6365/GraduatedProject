package com.example.graduatedproject.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.graduatedproject.Model.Likelist
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_liketopic.*
import kotlinx.android.synthetic.main.item_liketopic.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiketopicActivity : AppCompatActivity() {
    private val TAG = LiketopicActivity::class.java.simpleName
    val PREFERENCE = "SharedPreference"
    var liketopicList : ArrayList<Likelist>? = arrayListOf()

    //칩 그룹 지정
    var chipGroup: ChipGroup = liketopic_chipgroup
    var inflater: LayoutInflater = LayoutInflater.from(chipGroup.context)
    lateinit var chip: Chip
    lateinit var deletetopic: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liketopic)

        //서버에 보내야 할것 : 액세스토큰, 입력값
        //받아야 할것 : 관심주제검색어, 관심주제리스트
        //저장된 엑세스 토큰을 가져옴
        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()


        //엑세스토큰 서버에 보냄 -> 관심주제 리스트 받아오기 위해서
        ServerUtil.retrofitService.requestLikelist(accessToken)
            .enqueue(object : Callback<ArrayList<Likelist>> {
                override fun onResponse(call: Call<ArrayList<Likelist>>, response: Response<ArrayList<Likelist>>) {
                    if (response.isSuccessful) {
                        //서버로부터 받아서 리스트에 넣기
                        liketopicList = response.body()
                        Log.d(TAG, "관심주제리스트 받기 성공")

                        //chip에 넣기
                        for (i in 0..liketopicList!!.size) {
                            chip =
                                inflater.inflate(R.layout.item_liketopic, chipGroup, false) as Chip
                            chip.text = liketopicList!![i].name
                            chipGroup.addView(chip)
                        }
                        finish()
                    }
                }
                override fun onFailure(call: Call<ArrayList<Likelist>>, t: Throwable) {
                    Log.d(TAG, "관심주제리스트 받기 실패")
                    Toast.makeText(this@LiketopicActivity, "관심주제리스트 받기 실패", Toast.LENGTH_LONG)
                        .show()
                }
            })

        // 관심주제 추가 -> 검색
        liketopicplus_chip.setOnClickListener {
            val intent = Intent(this@LiketopicActivity, LiketopicSearchActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

            //키워드를 주고 화면전환이 되면
            //화면에 키워드 추가
        }

        // 관심주제 삭제
        // X버튼 누르면 -> LikedeleteDTO를 통해 키워드 서버로 보내기 -> 서버는 사용자의 관심리스트에서 삭제함
        // 해당 키워드 item 화면에 없애기
        liketopic_chip.setOnCloseIconClickListener {
            // 클릭된 chip에 있는 텍스트를 받아옴
            deletetopic = liketopic_chip.text.toString()
            for(i in 0 .. 10){
                //지우려는 주제 == 리스트의 태그이름과 같다면 코드수행
                if(deletetopic == liketopicList!![i].name){
                    var deletetagId = liketopicList!![i].tagId

                    val paramObject = JsonObject()
                    paramObject.addProperty("/users/tags/{tagId}", deletetagId)

                    //네트워크 통신 -> 서버에 accessToken, tagId 보냄
                    ServerUtil.retrofitService.requestLikedelete(accessToken, paramObject)
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Log.d(TAG, "관심주제 삭제 성공")
                                    // 해당 칩을 삭제함
                                    chipGroup.removeView(it)
                                    finish()
                                }
                            }
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.d(TAG, "관심주제 삭제 실패")
                                Toast.makeText(this@LiketopicActivity, "관심주제 삭제 실패", Toast.LENGTH_LONG)
                                    .show()
                            }
                        })
                }
            }


        }
    }
}