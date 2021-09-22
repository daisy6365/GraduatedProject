package com.example.graduatedproject.Activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.graduatedproject.Fragment.GroupMember
import com.example.graduatedproject.model.Group
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import kotlinx.android.synthetic.main.activity_study_group_apply.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StudyGroupApplyActivity : AppCompatActivity() {
    private val TAG = StudyGroupApplyActivity::class.java.simpleName
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
    var groupInfo = Group()
    var current_marker = MapPOIItem()
    var uLatitude : Double? = null
    var uLongitude : Double? = null
    lateinit var mapView : MapView
    lateinit var mapViewContainer : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_group_apply)

        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()
        val groupId = intent.getIntExtra("gatheringId",0)

        mapView = MapView(this)
        mapViewContainer = map_view
        mapViewContainer.addView(mapView)

        Log.d("gatheringId", groupId.toString())
        if (intent.hasExtra("gatheringId")) {
            val permissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                val lm: LocationManager =
                    getSystemService(Context.LOCATION_SERVICE) as LocationManager
                try {
                    ServerUtil.retrofitService.requestGroup(accessToken,groupId)
                        .enqueue(object : Callback<Group> {
                            override fun onResponse(call: Call<Group>, response: Response<Group>) {
                                if (response.isSuccessful) {
                                    groupInfo  = response.body()!!
                                    group_apply_on_off.setText(groupInfo!!.shape)
                                    current_people_number.setText(groupInfo.numberOfPeople.toString())

                                    val str = groupInfo.gatheringTime
                                    val idT: Int = str!!.indexOf("T")
                                    val date = str.substring(0,idT)
                                    val time = str.substring(idT+1)

                                    val dateList = date.split("-")
                                    val date_group = dateList[0] + "년 " + dateList[1] + "월 " + dateList[2] + "일"
                                    val timeList = time.split(":")
                                    val time_group = timeList[0] + "시 " + timeList[1] + "분"


                                    apply_group_date.setText(date_group)
                                    apply_group_time.setText(time_group)
                                    created_introduce_text.setText(groupInfo.content)
                                    if(groupInfo.place != null){
                                        location_detail_Info.setText(groupInfo.place!!.name)
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
                                        location_detail_Info.visibility = View.GONE
                                        map_view.visibility = View.GONE
                                    }

                                    if(groupInfo.apply == true){
                                        //지원한 상태 -> "지원하기" 버튼 지우고
                                        //"지원 취소"버튼 띄우기
                                        apply_group_btn.visibility = View.GONE
                                        apply_group_cancle_btn.visibility = View.VISIBLE
                                    }
                                    else if(groupInfo.apply == null){
                                        //모임 호스트 -> 지원하기 버튼 지우기
                                        apply_group_btn.visibility = View.GONE
                                        group_host_btn.visibility = View.VISIBLE
                                    }
                                    else{}//회원X,지원안한상태 -> 기존으로 두기

                                    Log.d(TAG, "회원 지역정보 조회 성공")
                                }
                            }
                            override fun onFailure(call: Call<Group>, t: Throwable) {
                                Log.d(TAG, "회원 지역정보 조회 실패")
                                Toast.makeText(this@StudyGroupApplyActivity, "회원 지역정보 조회 실패", Toast.LENGTH_LONG).show()
                            }
                        })
                    current_people_number.setOnClickListener {
                        //모임 참가자 멤버 조회
                        val dialog = GroupMember(groupId!!)
                        dialog.show(supportFragmentManager, "GroupMember")
                    }

                    apply_group_btn.setOnClickListener {
                        //참가 신청
                        ServerUtil.retrofitService.requestGroupApply(accessToken,groupId)
                            .enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Log.d(TAG, "모임참가 신청 성공")
                                        Toast.makeText(this@StudyGroupApplyActivity, "모임 참가신청을 했습니다.", Toast.LENGTH_LONG).show()

                                        apply_group_btn.visibility = View.GONE
                                        apply_group_cancle_btn.visibility = View.VISIBLE
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.d(TAG, "모임 참가 신청 실패")
                                    Toast.makeText(this@StudyGroupApplyActivity, "모임참가 신청 실패", Toast.LENGTH_LONG).show()
                                }
                            })
                    }

                    apply_group_cancle_btn.setOnClickListener {
                        //참가신청 취소
                        ServerUtil.retrofitService.requestGroupApplyCancel(accessToken,groupId)
                            .enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Log.d(TAG, "모임 참가신청 취소 성공")
                                        Toast.makeText(this@StudyGroupApplyActivity, "모임 참가신청을 취소했습니다.", Toast.LENGTH_LONG).show()

                                        apply_group_cancle_btn.visibility = View.GONE
                                        apply_group_btn.visibility = View.VISIBLE
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.d(TAG, "모임 참가신청 취소 실패")
                                    Toast.makeText(this@StudyGroupApplyActivity, "모임참가신청 취소 실패", Toast.LENGTH_LONG).show()
                                }
                            })
                    }

                    group_modify_btn.setOnClickListener {
                        //Activity 변경
                        mapViewContainer.removeView(mapView)
                        val intent = Intent(this@StudyGroupApplyActivity, StudyGroupModifyActivity::class.java)
                        intent.putExtra("gatheringId",groupId)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

                        finish()
                    }


                    group_delete_btn.setOnClickListener {
                        //모임 삭제
                        var builder = AlertDialog.Builder(this)
                        builder.setTitle("알림")
                        builder.setMessage("해당 모임을 삭제하시겠습니까?")
                        builder.setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                            ServerUtil.retrofitService.requestDeleteGroup(accessToken,groupId)
                                .enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.isSuccessful) {
                                            Log.d(TAG, "모임 삭제 성공")
                                            Toast.makeText(this@StudyGroupApplyActivity, "모임을 삭제했습니다.", Toast.LENGTH_LONG).show()

                                            mapViewContainer.removeView(mapView)
                                            finish()
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        Log.d(TAG, "모임 삭제 실패")
                                        Toast.makeText(this@StudyGroupApplyActivity, "모임 삭제 실패", Toast.LENGTH_LONG).show()
                                    }
                                })
                        })
                        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                            Log.d(TAG, "취소")
                        })
                        builder.show()

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

    }
}
