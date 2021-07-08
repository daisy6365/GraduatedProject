package com.example.graduatedproject.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.graduatedproject.Model.Image
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
    var profile = Profile()
    var location : Location? = null

    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        if (intent.hasExtra("modify_item")) {
            modifyLocation(accessToken)
            Mylocation(accessToken)
        }
        else {
            Mylocation(accessToken)
        }

        place_modify_btn.setOnClickListener {
            //Activity 변경
            val intent = Intent(this@MapActivity, MapSearchActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }

    fun Mylocation(accessToken : String){
        ServerUtil.retrofitService.requestProfile(accessToken)
            .enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        //프로필에서 회원의 locationId를 받아옴
                        profile = response.body()!!

                        if(profile.locationId == null){
                            profile.locationId = 1
                        }
                        else{}
                        Log.d("내지역불러오기", profile.locationId.toString())
                        ServerUtil.retrofitService.requestLocationId(accessToken, profile.locationId)
                            .enqueue(object : Callback<Location> {
                                override fun onResponse(call: Call<Location>, response: Response<Location>) {
                                    if (response.isSuccessful) {
                                        //회원의 지역정보를 받아옴
                                        location  = response.body()!!
                                        Log.d("location 회원 지역정보 조회", response.body().toString())

                                        my_place.setText(location!!.dong)

                                        Log.d(TAG, "회원 지역정보 조회 성공")
                                    }
                                }
                                override fun onFailure(call: Call<Location>, t: Throwable) {
                                    Log.d(TAG, "회원 지역정보 조회 실패")
                                    Toast.makeText(this@MapActivity, "회원 지역정보 조회 실패", Toast.LENGTH_LONG).show()
                                }
                            })
                    }
                }
                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.d(TAG, "회원 지역Id조회 실패")
                    Toast.makeText(this@MapActivity, "회원 지역Id조회 실패", Toast.LENGTH_LONG)
                        .show()
                }
            })

    }
    fun modifyLocation(accessToken : String){
        val new_locationId = intent.getIntExtra("modify_item",0)
        Log.d("지역액티비티",new_locationId.toString())
        ServerUtil.retrofitService.requestLocationmodify(accessToken,new_locationId)
            .enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "지역정보수정 성공")
                    }

                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.d(TAG, "지역정보수정 실패")
                    Toast.makeText(this@MapActivity, "지역정보수정 실패", Toast.LENGTH_LONG).show()
                }
            })
    }

}