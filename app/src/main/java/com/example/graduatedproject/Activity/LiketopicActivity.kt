package com.example.graduatedproject.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.graduatedproject.Model.Likelist
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_liketopic.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.item_liketopic.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.zip.Inflater

class LiketopicActivity : AppCompatActivity() {
    private val TAG = LiketopicActivity::class.java.simpleName
    var liketopicList : ArrayList<Likelist>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liketopic)

        //서버에 보내야 할것 : 액세스토큰, 입력값
        //받아야 할것 : 관심주제검색어, 관심주제리스트
        //저장된 엑세스 토큰을 가져옴
        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        if (intent.hasExtra("add_item")) { addLikeTag(accessToken) }
        else{ myTag(accessToken) }

        // 관심주제 추가 -> 검색
        liketopicplus_chip.setOnClickListener {
            val intent = Intent(this@LiketopicActivity, LiketopicSearchActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

        }
    }

    fun myTag(accessToken : String){
        //칩 그룹 지정
        var chipGroup: ChipGroup = liketopic_chipgroup
        var inflater: LayoutInflater = LayoutInflater.from(chipGroup.context)

        //엑세스토큰 서버에 보냄 -> 관심주제 리스트 받아오기 위해서
        ServerUtil.retrofitService.requestLikelist(accessToken)
            .enqueue(object : Callback<ArrayList<Likelist>> {
                override fun onResponse(call: Call<ArrayList<Likelist>>, response: Response<ArrayList<Likelist>>) {
                    if (response.isSuccessful) {
                        //서버로부터 받아서 리스트에 넣기
                        liketopicList = response.body()
                        Log.d(TAG, "관심주제리스트 받기 성공")

                        //chip에 넣기
                        for (i in 0..liketopicList!!.size-1) {
                            var chip : Chip = inflater.inflate(R.layout.item_liketopic, chipGroup, false) as Chip
                            chip.text = liketopicList!![i].name
                            chipGroup.addView(chip)

                            // chip 닫기 버튼을 눌렀을때
                            // 텍스트를 받아와서 서버로 보냄
                            chip.setOnCloseIconClickListener {
                                var deletetopic = (it as TextView).text.toString()
                                val findDeleteTopic = liketopicList!!.find({ it.name.equals(deletetopic) })

                                ServerUtil.retrofitService.requestLikeDelete(accessToken, findDeleteTopic!!.tagId)
                                    .enqueue(object : Callback<Void> {
                                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                            if (response.isSuccessful) {
                                                Log.d(TAG, "관심주제 삭제 성공")
                                                // 해당 칩을 삭제함
                                                chipGroup.removeView(it)
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
                override fun onFailure(call: Call<ArrayList<Likelist>>, t: Throwable) {
                    Log.d(TAG, "관심주제리스트 받기 실패")
                    Toast.makeText(this@LiketopicActivity, "관심주제리스트 받기 실패", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    fun addLikeTag(accessToken : String){
        val new_addtag = intent.getIntExtra("add_item",0)

        ServerUtil.retrofitService.requestLikeadd(accessToken,new_addtag)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "관심태그추가 성공")
                        myTag(accessToken)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(TAG, "관심태그추가 실패")
                    Toast.makeText(this@LiketopicActivity, "관심태그추가 실패", Toast.LENGTH_LONG).show()
                }
            })
    }
}