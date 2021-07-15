package com.example.graduatedproject.Activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.graduatedproject.Model.MapCode
import com.example.graduatedproject.Model.MyLocation
import com.example.graduatedproject.Util.ServerUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_liketopic.*
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_study_create.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class StudyCreateActivity : AppCompatActivity(), MapView.MapViewEventListener {
    private val TAG = StudyCreateActivity::class.java.simpleName
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
    lateinit var reverseGeoCoder : MapReverseGeoCoder
    var new_imageUrl : Uri? = null
    var new_imageUrlPath : String? = null
    var imageFile : File? = null
    var current_marker = MapPOIItem()
    var people_num : Int = 0
    //태그 넣을 리스트
    var tagArray = arrayListOf<String>()
    var offline : Boolean = true
    var online : Boolean = true
    var uLatitude : Double? = null
    var uLongitude : Double? = null
    var locationCode : String? = null


    private val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            //결과값이 사진을 선택했을때
            new_imageUrl = result.data?.data!!
            Glide.with(this)
                .load(new_imageUrl)
                .centerCrop()
                .error(com.example.graduatedproject.R.drawable.cover)
                .into(create_study_cover_img)
            //절대경로변환 함수 호출
            new_imageUrlPath= absolutelyPath( this ,new_imageUrl!!)
            imageFile = File(new_imageUrlPath)
        } else {
            //취소했을때
            Toast.makeText(this@StudyCreateActivity, "사진 선택 취소", Toast.LENGTH_LONG).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.graduatedproject.R.layout.activity_study_create)

        //칩 그룹 지정
        var chipGroup: ChipGroup = create_chip_group
        var inflater: LayoutInflater = LayoutInflater.from(chipGroup.context)

        val mapView = MapView(this)
        val mapViewContainer = map_view
        mapViewContainer.addView(mapView)

        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {

                val userNowLocation: Location? =
                    lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                //현재위치 가져오기
                uLatitude = userNowLocation!!.latitude
                uLongitude = userNowLocation!!.longitude
                val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)
                mapView.setMapCenterPoint(uNowPosition, true)

                //현재위치(경도, 위도)를 기반으로 마커 찍기

                current_marker.itemName = "현재 위치로 스터디장소 설정"
                current_marker.tag = 0
                current_marker.mapPoint = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)
                current_marker.isMoveToCenterOnSelect
                current_marker.markerType = MapPOIItem.MarkerType.BluePin
                current_marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                mapView.addPOIItem(current_marker)

                //지도에서 원하는 곳 클릭하면
                //기존의 마커 삭제하고 새로운 마커 추가하기
                //새로운 마커의 경도, 위도 가져오기

                mapView.setMapViewEventListener(this)

                //이미지 갤러리에서 받아오기 -> 붙이기
                change_study_cover_btn.setOnClickListener {
                    //갤러리 열어서 사진url 받기 -> 절대경로로 변환
                    openGalley()
                }
                number_minus.setOnClickListener {
                    if(people_num > 2){
                        people_num--
                        people_number.text = people_num.toString()
                    }
                    else{
                        Toast.makeText(this@StudyCreateActivity, "스터디인원은 최소 2명이여야 합니다.", Toast.LENGTH_LONG).show()
                    }
                }
                number_plus.setOnClickListener {
                    people_num++
                    people_number.text = people_num.toString()
                }
                add_tag_btn.setOnClickListener{
                    //배열에 관심태그 추가 하고 칩 만들기
                    addTag(chipGroup,inflater)
                }

                //Todo 스피너설정

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

                register_study.setOnClickListener {
                    if(offline == false && online == false){
                        Toast.makeText(this, "ON/OFFLINE을 체크해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        sendStudyInfo()
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

    fun addTag(chipGroup : ChipGroup, inflater: LayoutInflater): ArrayList<String> {
        var chip : Chip = inflater.inflate(com.example.graduatedproject.R.layout.item_liketopic, chipGroup, false) as Chip

        tagArray.add(edit_tag.text.toString())
        chip.text = edit_tag.text.toString()
        chipGroup.addView(chip)

        Log.d("관심주제 리스트", tagArray.toString())
        chip.setOnCloseIconClickListener {
            for(i in 0 .. 6) {
                tagArray.remove((it as TextView).text.toString())
                chipGroup.removeView(it)
                Log.d("관심주제 리스트", tagArray.toString())
            }
        }
        return tagArray
    }
    private fun sendStudyInfo(){
        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()



        //스터디이름,참여인원, 해시태그 리스트, 카테고리2개, 소개내용, 온/오프라인 여부, locationId 정보 가져오기
        var studyName = edit_study_name.text.toString()
        var numberOfPeople = people_number.text.toString()
        var content = create_introduce_text.text.toString()
        var tags = tagArray.toString()
        var categoryId = 2
        locationCode


        lateinit var requestImg: RequestBody
        var imageBitmap : MultipartBody.Part? = null

//        Log.d("스터디생성정보", studyName.toString())
//        Log.d("스터디생성정보", numberOfPeople.toString())
//        Log.d("스터디생성정보", content.toString())
//        Log.d("스터디생성정보", tags.toString())
//        Log.d("스터디생성정보", online.toString())
//        Log.d("스터디생성정보", offline.toString())
//        Log.d("스터디생성정보", categoryId.toString())
        Log.d("스터디생성정보 -> 지역코드", locationCode.toString())

        if(imageFile != null){
            requestImg= RequestBody.create(MediaType.parse("image/*"),imageFile)
            imageBitmap = MultipartBody.Part.createFormData("image", imageFile?.getName(), requestImg)

        }else{}

        val paramObject = JsonObject()
        paramObject.addProperty("name", studyName)
        paramObject.addProperty("nickName", numberOfPeople)
        paramObject.addProperty("nickName", content)
        paramObject.addProperty("nickName", tags)
        paramObject.addProperty("nickName", online)
        paramObject.addProperty("nickName", offline)
        //paramObject.addProperty("nickName", newNickname)
        paramObject.addProperty("nickName", categoryId)


        val request = RequestBody.create(MediaType.parse("application/json"),paramObject.toString())

        //locationCode 가져오기
//        ServerUtil.retrofitService.SendCreateStudyInfo(accessToken, imageBitmap, request)
//            .enqueue(object : Callback<Study> {
//                override fun onResponse(call: Call<Study>, response: Response<Study>) {
//                    if (response.isSuccessful) {
//
//                        Log.d(TAG, "스터디생성정보 전송 성공")
//
//
//                        val intent = Intent(this@StudyCreateActivity, StudyApplyActivity::class.java)
//                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//
//                    }
//                }
//                override fun onFailure(call: Call<Study>, t: Throwable) {
//                    Log.d(TAG, "스터디생성정보 전송 실패")
//                    Toast.makeText(this@StudyCreateActivity, "스터디생성정보 전송 실패", Toast.LENGTH_LONG).show()
//                }
//            })

    }
    //앱갤러리 열기
    private fun openGalley() {
        //갤러리에 접근하도록 함 -> 갤러리를 엶
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getImage.launch(intent)
    }
    //저장한 선택사진의 경로를 절대경로로 바꿈
    fun absolutelyPath(context : Context, new_imageUrl: Uri): String {
        val contentResolver = context!!.contentResolver?: return null.toString()

        val filePath = context!!.applicationInfo.dataDir + File.separator +
                System.currentTimeMillis()
        val file = File(filePath)

        try {
            val inputStream = contentResolver.openInputStream(new_imageUrl) ?: return null.toString()
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int

            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)

            outputStream.close()
            inputStream.close()

        } catch (e: IOException) {
            return null.toString()
        }
        val result = file.absolutePath
        return result
    }
    //LocationId 찾기
    fun findLocationId(code : String){
        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()
        val paramObject = JsonObject()
        paramObject.addProperty("code",code)

        ServerUtil.retrofitService.requestLocationId(accessToken, paramObject.get("code").asString)
            .enqueue(object : Callback<MyLocation> {
                override fun onResponse(call: Call<MyLocation>, response: Response<MyLocation>) {
                    if (response.isSuccessful) {
                        //행정동&법정동 코드를 통해 서버의 지역정보Id를 받아옴
                        val myLocation  = response.body()!!
                        val locationId = myLocation.id

                    }
                }
                override fun onFailure(call: Call<MyLocation>, t: Throwable) {
                    Log.d(TAG, "회원 지역정보ID 조회 실패")
                    Toast.makeText(this@StudyCreateActivity, "회원 지역정보ID 조회 실패", Toast.LENGTH_LONG).show()
                }
            })
    }
    //카테고리 리스트 가져오기
    fun getCategoryList(){

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
            var kakaoToken = "KakaoAK 9a9a02eedbe752442e4a7550ec217f88"

            // 클릭한 위치에 마커와 주소 보이기
            reverseGeoCoder = MapReverseGeoCoder("35e1ae8dad57b1dfb6a8f38f6903c184", p1!!,
                object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
                    override fun onReverseGeoCoderFoundAddress(mapReverseGeoCoder: MapReverseGeoCoder, s: String) {
                        current_marker.itemName = s
                    }

                    override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {
                        Log.e("Err", "GeoCoding")
                        Toast.makeText(this@StudyCreateActivity, "주소를 찾지 못하였습니다.", Toast.LENGTH_LONG).show()
                    }
                }, this
            )
            current_marker.mapPoint = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)
            p0!!.addPOIItem(current_marker)

            Log.d("마커 찍은 주소", p1.toString())

            ServerUtil.kakaoService.requestCode(kakaoToken, uLongitude!!, uLatitude!!)
                .enqueue(object : Callback<MapCode> {
                    override fun onResponse(call: Call<MapCode>, response: Response<MapCode>) {
                        if (response.isSuccessful) {
                            //행정동
                            if(response.body()?.documents?.size == 2){
                                locationCode = response.body()?.documents?.get(1)?.code
                                Log.d( "회원 지역CODE",locationCode.toString())
                            }
                            //법정동
                            else{
                                locationCode = response.body()?.documents?.get(0)?.code
                                Log.d( "회원 지역CODE",locationCode.toString())
                            }
                        }
                    }
                    override fun onFailure(call: Call<MapCode>, t: Throwable) {
                        Log.d(TAG, "회원 지역code 조회 실패")
                        Toast.makeText(this@StudyCreateActivity, "회원 지역정보code 조회 실패", Toast.LENGTH_LONG).show()
                    }
                })

        }
    }
    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}
    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}

}