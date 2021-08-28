package com.example.graduatedproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.graduatedproject.Model.Group
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import kotlinx.android.synthetic.main.activity_study_group_apply.*
import kotlinx.android.synthetic.main.activity_study_group_create.*
import kotlinx.android.synthetic.main.activity_study_group_modify.*
import kotlinx.android.synthetic.main.activity_study_group_modify.check_offline
import kotlinx.android.synthetic.main.activity_study_group_modify.check_online
import kotlinx.android.synthetic.main.activity_study_group_modify.edit_day
import kotlinx.android.synthetic.main.activity_study_group_modify.edit_hour
import kotlinx.android.synthetic.main.activity_study_group_modify.edit_min
import kotlinx.android.synthetic.main.activity_study_group_modify.edit_month
import kotlinx.android.synthetic.main.activity_study_group_modify.edit_year
import kotlinx.android.synthetic.main.activity_study_group_modify.map_view
import kotlinx.android.synthetic.main.activity_study_group_modify.mapview_notice
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyGroupModifyActivity : AppCompatActivity(), MapView.MapViewEventListener {
    private val TAG = StudyGroupModifyActivity::class.java.simpleName
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
    var current_marker = MapPOIItem()
    var groupInfo = Group()
    lateinit var reverseGeoCoder : MapReverseGeoCoder
    var uLatitude : Double? = null
    var uLongitude : Double? = null
    var offline : Boolean = false
    var online : Boolean = false
    var year : String? = null
    var month : String? = null
    var day : String? = null
    var hour : String? = null
    var min : String? = null
    lateinit var mapView : MapView
    lateinit var mapViewContainer : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_group_modify)

        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()
        val groupId = intent.getIntExtra("gatheringId",0)

        mapView = MapView(this)
        mapViewContainer = map_view
        mapViewContainer.addView(mapView)

        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {

                // 모임정보 화면에 셋팅
                ServerUtil.retrofitService.requestGroup(accessToken,groupId)
                    .enqueue(object : Callback<Group> {
                        override fun onResponse(call: Call<Group>, response: Response<Group>) {
                            if (response.isSuccessful) {
                                groupInfo  = response.body()!!

                                if(groupInfo.shape == "OFFLINE"){
                                    check_offline.isChecked = true
                                }else{
                                    check_online.isChecked = true
                                }

                                val str = groupInfo.gatheringTime
                                val idT: Int = str!!.indexOf("T")
                                val date = str.substring(0,idT)
                                val time = str.substring(idT+1)

                                val dateList = date.split("-")
                                val timeList = time.split(":")


                                edit_year.setText(dateList[0])
                                edit_month.setText(dateList[1])
                                edit_day.setText(dateList[2])
                                edit_hour.setText(timeList[0])
                                edit_min.setText(timeList[1])

                                modify_todo_edit.setText(groupInfo.content)
                                if(groupInfo.place != null){
                                    modify_detail_place_edit.setText(groupInfo.place!!.name)
                                    //위치 가져오기
                                    uLatitude = groupInfo.place!!.let
                                    uLongitude = groupInfo.place!!.len

                                    val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)
                                    mapView.setMapCenterPoint(uNowPosition, true)

                                    //현재위치(경도, 위도)를 기반으로 마커 찍기
                                    current_marker.itemName = "모임 장소"
                                    current_marker.tag = 0
                                    current_marker.mapPoint = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)
                                    current_marker.isMoveToCenterOnSelect
                                    current_marker.markerType = MapPOIItem.MarkerType.BluePin
                                    current_marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                                    mapView.addPOIItem(current_marker)
                                }
                                else{
                                    modify_detail_place_edit.visibility = View.GONE
                                    map_view.visibility = View.GONE
                                    mapview_notice.visibility = View.GONE
                                }

                                Log.d(TAG, "회원 지역정보 조회 성공")
                            }
                        }
                        override fun onFailure(call: Call<Group>, t: Throwable) {
                            Log.d(TAG, "회원 지역정보 조회 실패")
                            Toast.makeText(this@StudyGroupModifyActivity, "회원 지역정보 조회 실패", Toast.LENGTH_LONG).show()
                        }
                    })

                //지도에서 원하는 곳 클릭하면
                //기존의 마커 삭제하고 새로운 마커 추가하기
                //새로운 마커의 경도, 위도 가져오기
                mapView.setMapViewEventListener(this)

                check_offline.setOnCheckedChangeListener { buttonView, isChecked ->
                    if(isChecked){
                        offline = true

                    }
                    else{
                        offline = false
                    }
                }
                check_online.setOnCheckedChangeListener { buttonView, isChecked ->
                    if(isChecked){
                        online = true

                    }
                    else{
                        online = false
                    }
                }

                modified_group_btn.setOnClickListener {
                    year = edit_year.text.toString()
                    month = edit_month.text.toString()
                    day = edit_day.text.toString()
                    hour = edit_hour.text.toString()
                    min = edit_min.text.toString()

                    if(offline == false && online == false){
                        Toast.makeText(this, "ON/OFFLINE을 체크해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    else if(online == true && offline == true){
                        Toast.makeText(this, "ON/OFFLINE중 하나만 체크해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    else if(year == null || month == null || day == null || hour == null || min == null){
                        Toast.makeText(this, "시간을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        sendModifiedStudyInfo(groupId)
                    }

                }


            } catch (e: NullPointerException) {
                Log.e("LOCATION_ERROR", e.toString())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.finishAffinity(this)
                } else {
                    ActivityCompat.finishAffinity(this)
                }
                val intent = Intent(this, MainActivity::class.java)
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
    private fun sendModifiedStudyInfo(groupId : Int){
        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()
        lateinit var state : String

        if(online == true){
            state = "ONLINE"
        }else if(offline == true){
            state = "OFFLINE"
        }else{ }

        var todoContent = modify_todo_edit.text.toString()
        var placeContent = modify_detail_place_edit.text.toString()
        if(month!!.length < 2){
            month = "0" + month
        }
        if(day!!.length < 2){
            day = "0" + day
        }
        if(hour!!.length < 2){
            hour = "0" + hour
        }
        if(min!!.length < 2){
            min = "0" + min
        }
        var gatheringTime = year+ "-" + month +"-"+ day + "T" + hour + ":" + min + ":00"
        var params:HashMap<String, Any> = HashMap<String, Any>()
        params.put("gatheringTime", gatheringTime)
        params.put("shape", state)
        params.put("content", todoContent)
        if(groupInfo.place != null){
            params.put("placeName", placeContent)
            params.put("let", uLatitude!!)
            params.put("len", uLongitude!!)
        }else{}


        ServerUtil.retrofitService.requestModifyGroup(accessToken, groupId, params)
            .enqueue(object : Callback<Group> {
                override fun onResponse(call: Call<Group>, response: Response<Group>) {
                    if (response.isSuccessful) {
                        var groupId : Int = response.body()!!.id

                        Log.d(TAG, "모임 생성정보 전송 성공")

                        mapViewContainer.removeView(mapView)
                        val intent = Intent(this@StudyGroupModifyActivity, StudyGroupApplyActivity::class.java)
                        intent.putExtra("gatheringId",groupId)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                }
                override fun onFailure(call: Call<Group>, t: Throwable) {
                    Log.d(TAG, "모임 생성정보 전송 실패")
                    Toast.makeText(this@StudyGroupModifyActivity, "스터디생성정보 전송 실패", Toast.LENGTH_LONG).show()
                }
            })

    }
    override fun onMapViewInitialized(p0: MapView?) {}
    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {}
    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?){
        if (p1 != null) {
            p0?.removeAllPOIItems()           // 이전 마커 삭제하기
            // 지도상 위경도 얻기
            uLatitude = p1!!.mapPointGeoCoord.latitude
            uLongitude = p1!!.mapPointGeoCoord.longitude
            Log.d("마커 찍은 주소uLatitude", uLatitude.toString())
            Log.d("마커 찍은 주소uLongitude", uLongitude.toString())

            // 클릭한 위치에 마커와 주소 보이기
            reverseGeoCoder = MapReverseGeoCoder("35e1ae8dad57b1dfb6a8f38f6903c184", p1!!,
                object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
                    override fun onReverseGeoCoderFoundAddress(mapReverseGeoCoder: MapReverseGeoCoder, s: String) {
                        current_marker.itemName = s
                    }

                    override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {
                        Log.e("Err", "GeoCoding")
                        Toast.makeText(this@StudyGroupModifyActivity, "주소를 찾지 못하였습니다.", Toast.LENGTH_LONG).show()
                    }
                }, this
            )
            current_marker.mapPoint = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)
            p0!!.addPOIItem(current_marker)

            Log.d("마커 찍은 주소", p1.toString())

        }
    }
    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}
}