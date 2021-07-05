package com.example.graduatedproject.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.graduatedproject.Model.Location
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import kotlinx.android.synthetic.main.activity_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity() {
    private val TAG = MapActivity::class.java.simpleName

    //Todo 위치정보 제공에 동의 여부
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        if (intent.hasExtra("modify_item"))
        {
            val new_locationId = intent.getIntExtra("modify_item",0)

            ServerUtil.retrofitService.requestLocationmodify(accessToken,new_locationId)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "지역정보수정 성공")
                        }
                        finish()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d(TAG, "지역정보수정 실패")
                        Toast.makeText(this@MapActivity, "지역정보수정 실패", Toast.LENGTH_LONG).show()
                    }
                })
        }

        else { }

        //서버에 보내야 할것 : 액세스토큰
        //받아야 할것 : 사용자의 설정위치
        //저장된 엑세스 토큰을 가져옴

        var profile : Profile?
        var locationId : Int = 0
        ServerUtil.retrofitService.requestProfile(accessToken)
            .enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        //프로필에서 회원의 locationId를 받아옴
                        profile = response.body()
                        locationId = profile!!.locationId

                        Log.d(TAG, "회원 지역Id조회 성공")

                        finish()
                    }
                }
                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.d(TAG, "회원 지역Id조회 실패")
                    Toast.makeText(this@MapActivity, "회원 지역Id조회 실패", Toast.LENGTH_LONG)
                        .show()
                }
            })

        var locationList : Location?
        var locationName : String
        ServerUtil.retrofitService.requestLocationId(accessToken,locationId)
            .enqueue(object : Callback<Location> {
                override fun onResponse(call: Call<Location>, response: Response<Location>) {
                    if (response.isSuccessful) {
                        //프로필에서 회원의 locationId를 받아옴
                        locationList = response.body()
                        locationName = locationList!!.dong

                        my_place.setText(locationName)

                        Log.d(TAG, "회원 지역정보 조회 성공" +
                                "")
                    }
                }
                override fun onFailure(call: Call<Location>, t: Throwable) {
                    Log.d(TAG, "회원 지역정보 조회 실패")
                    Toast.makeText(this@MapActivity, "회원 지역정보 조회 실패", Toast.LENGTH_LONG)
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