package com.example.graduatedproject.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import kotlinx.android.synthetic.main.activity_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity() {
    private val TAG = MapActivity::class.java.simpleName
    val PREFERENCE = "SharedPreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        //서버에 보내야 할것 : 액세스토큰
        //받아야 할것 : 사용자의 설정위치
        //저장된 엑세스 토큰을 가져옴
        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestLocation(accessToken)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "관심주제 삭제 성공")
                        finish()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(TAG, "관심주제 삭제 실패")
                    Toast.makeText(this@MapActivity, "관심주제 삭제 실패", Toast.LENGTH_LONG)
                        .show()
                }
            })

        place_modify_btn.setOnClickListener {
            //Activity 변경
            val intent = Intent(this@MapActivity, MapSearchActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))



        }

    }
}