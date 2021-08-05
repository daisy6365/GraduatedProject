package com.example.graduatedproject.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.graduatedproject.Model.MapCode
import com.example.graduatedproject.Model.MyLocation
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity() {
    private val TAG = MapActivity::class.java.simpleName
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
    var profile = Profile()
    var location : MyLocation? = null

    var locationManager : LocationManager? = null
    var latitudeY : Double? = null
    var longitudeX : Double? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                if (intent.hasExtra("modify_item")) {
                    val new_locationId = intent.getIntExtra("modify_item",0)
                    modifyLocation(accessToken,new_locationId) }
                else { Mylocation(accessToken) }

                place_modify_btn.setOnClickListener {
                    //Activity 변경
                    val intent = Intent(this@MapActivity, MapSearchActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }

                currentplace_search_btn.setOnClickListener {
                    findMyGPS()
                }

            } catch (e: NullPointerException) {
                Log.e("LOCATION_ERROR", e.toString())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.finishAffinity(this)
                } else {
                    ActivityCompat.finishAffinity(this)
                }
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)

                System.exit(0)

            }
        } else {
            Toast.makeText(this, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                PERMISSIONS_REQUEST_CODE
            )
        }

    }

    fun Mylocation(accessToken : String){
        ServerUtil.retrofitService.requestProfile(accessToken)
            .enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        //프로필에서 회원의 locationId를 받아옴
                        profile = response.body()!!

                        if(profile.locationId == 0){
                            profile.locationId = 1
                        }
                        else{}
                        Log.d("내지역불러오기", profile.locationId.toString())
                        ServerUtil.retrofitService.requestLocation(accessToken, profile.locationId)
                            .enqueue(object : Callback<MyLocation> {
                                override fun onResponse(call: Call<MyLocation>, response: Response<MyLocation>) {
                                    if (response.isSuccessful) {
                                        //회원의 지역정보를 받아옴
                                        location  = response.body()!!
                                        Log.d("location 회원 지역정보 조회", response.body().toString())

                                        my_place.setText(location!!.dong)

                                        Log.d(TAG, "회원 지역정보 조회 성공")
                                    }
                                }
                                override fun onFailure(call: Call<MyLocation>, t: Throwable) {
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
    fun modifyLocation(accessToken : String,new_locationId:Int){
        Log.d("지역액티비티",new_locationId.toString())
        ServerUtil.retrofitService.requestLocationmodify(accessToken,new_locationId)
            .enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "지역정보수정 성공")
                        Mylocation(accessToken)
                    }

                }
                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.d(TAG, "지역정보수정 실패")
                    Toast.makeText(this@MapActivity, "지역정보수정 실패", Toast.LENGTH_LONG).show()
                }
            })
    }

    fun findMyGPS(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        var userLocation: Location = getLatLng()
        if(userLocation != null){
            //위도, 경도를 받아옴
            latitudeY = userLocation.latitude
            longitudeX = userLocation.longitude
            Log.d("CheckCurrentLocation", "현재 내 위치 값: ${latitudeY}, ${longitudeX}")

            var mGeoCoder =  Geocoder(applicationContext, Locale.KOREAN)
            var mResultList: List<Address>? = null
            try{
                mResultList = mGeoCoder.getFromLocation(
                    latitudeY!!, longitudeX!!, 1
                )
            }catch(e: IOException){
                e.printStackTrace()
            }
            if(mResultList != null){
                Log.d("CheckCurrentLocation", mResultList[0].getAddressLine(0))
            }

        }
        findMyKakaoCode(longitudeX!!, latitudeY!!)
    }
    fun findMyKakaoCode(longitudeX : Double,latitudeY : Double){
        var kakaoToken = "KakaoAK 9a9a02eedbe752442e4a7550ec217f88"

        ServerUtil.kakaoService.requestCode(kakaoToken, longitudeX,latitudeY)
            .enqueue(object : Callback<MapCode> {
                override fun onResponse(call: Call<MapCode>, response: Response<MapCode>) {
                    if (response.isSuccessful) {
                        //GPS를 통해 행정/법정동 코드를 받아옴


                        if(response.body()?.documents?.size == 2){
                            var code_one = response.body()?.documents?.get(1)?.code
                            findLocationId(code_one.toString())
                            Log.d( "회원 지역CODE",code_one.toString())
                        }
                        else{
                            var code_zero = response.body()?.documents?.get(0)?.code
                            findLocationId(code_zero.toString())
                            Log.d( "회원 지역CODE",code_zero.toString())
                        }
                    }
                }
                override fun onFailure(call: Call<MapCode>, t: Throwable) {
                    Log.d(TAG, "회원 지역code 조회 실패")
                    Toast.makeText(this@MapActivity, "회원 지역정보code 조회 실패", Toast.LENGTH_LONG).show()
                }
            })
    }
    fun findLocationId(code : String){
        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()
        val paramObject = JsonObject()
        paramObject.addProperty("code",code)

        ServerUtil.retrofitService.requestLocationId(accessToken,paramObject.get("code").asString)
            .enqueue(object : Callback<MyLocation> {
                override fun onResponse(call: Call<MyLocation>, response: Response<MyLocation>) {
                    if (response.isSuccessful) {
                        //행정동/법정동 코드를 통해 서버의 지역정보Id를 받아옴
                        val myLocation  = response.body()!!
                        val locationId = myLocation.id

                        modifyLocation(accessToken,locationId)

                    }
                }
                override fun onFailure(call: Call<MyLocation>, t: Throwable) {
                    Log.d(TAG, "회원 지역정보ID 조회 실패")
                    Toast.makeText(this@MapActivity, "회원 지역정보ID 조회 실패", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun getLatLng(): Location{
        lateinit var currentLatLng: Location
        var hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
        var hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION)

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            val locatioNProvider = LocationManager.GPS_PROVIDER
            currentLatLng = locationManager?.getLastKnownLocation(locatioNProvider)!!
        }else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            currentLatLng = getLatLng()
        }
        return currentLatLng!!
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size){
            var check_result = true
            for(result in grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }
            if(check_result){
            }else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])){
                    Toast.makeText(this, "권한 설정이 거부되었습니다.\n앱을 사용하시려면 다시 실행해주세요.", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    Toast.makeText(this, "권한 설정이 거부되었습니다.\n설정에서 권한을 허용해야 합니다..", Toast.LENGTH_SHORT).show()
                }
            }
            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}