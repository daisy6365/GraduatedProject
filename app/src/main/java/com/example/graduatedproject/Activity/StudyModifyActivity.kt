package com.example.graduatedproject.Activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.graduatedproject.Model.Category
import com.example.graduatedproject.Model.Study
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.activity_study_modify.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class StudyModifyActivity : AppCompatActivity() {
    private val TAG = StudyModifyActivity::class.java.simpleName
    var studyInfo = Study()
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
    var new_imageUrl : Uri? = null
    var new_imageUrlPath : String? = null
    var imageFile : File? = null
    var current_marker = MapPOIItem()
    lateinit var reverseGeoCoder : MapReverseGeoCoder
    var people_num : Int = 0
    var tagArray = arrayListOf<String>()
    var offline : Boolean = false
    var online : Boolean = false
    var uLatitude : Double? = null
    var uLongitude : Double? = null
    var locationCode : String? = null
    private lateinit var spinnerAdapterparent : SpinnerAdapter
    private lateinit var spinnerAdapterchild : SpinnerAdapter
    var categoryListParent : ArrayList<Category>? = null
    var categoryListChild : ArrayList<Category>? = null
    var categoryParent: MutableList<String> = mutableListOf("큰 카테고리")
    var categoryChild : MutableList<String> =  mutableListOf("작은 카테고리")
    var seletedParent : String? = null
    var seletedChild : String? = null
    var categorychildSeletedItem : String? = null


    private val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            //결과값이 사진을 선택했을때
            new_imageUrl = result.data?.data!!
            Glide.with(this)
                .load(new_imageUrl)
                .centerCrop()
                .error(com.example.graduatedproject.R.drawable.cover)
                .into(modify_study_cover_img)
            //절대경로변환 함수 호출
            new_imageUrlPath= absolutelyPath( this ,new_imageUrl!!)
            imageFile = File(new_imageUrlPath)
        } else {
            //취소했을때
            Toast.makeText(this@StudyModifyActivity, "사진 선택 취소", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_modify)

        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        //칩 그룹 지정
        var chipGroup: ChipGroup = modify_chip_group
        var inflater: LayoutInflater = LayoutInflater.from(chipGroup.context)

        val mapView = MapView(this)
        val mapViewContainer = modify_map_view
        mapViewContainer.addView(mapView)

        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                //정보 불러와서 화면에 정보들 붙이기
                val studyId = intent.getIntExtra("studyId",0)
                setStudyInfo(studyId)

                for(i in 0 .. tagArray.size-1){
                    var oldchip : Chip = inflater.inflate(R.layout.item_liketopic, chipGroup, false) as Chip
                    oldchip.text = tagArray[i]
                    chipGroup.addView(oldchip)
                }


                //기능 수행
                modify_study_cover_btn.setOnClickListener {
                    //갤러리 열어서 사진url 받기 -> 절대경로로 변환
                    openGalley()
                }

                modify_number_minus.setOnClickListener {
                    if(people_num > 2){
                        people_num--
                        modify_people_number.text = people_num.toString()
                    }
                    else{
                        Toast.makeText(this@StudyModifyActivity, "스터디인원은 최소 2명이여야 합니다.", Toast.LENGTH_LONG).show()
                    }
                }
                modify_number_plus.setOnClickListener {
                    people_num++
                    modify_people_number.text = people_num.toString()
                }
                add_tag_btn.setOnClickListener {
                    //배열에 관심태그 추가 하고 칩 만들기
                    addTag(chipGroup,inflater)
                    modify_edit_tag.setText(null)
                    modify_edit_tag.setHint("관련태그 입력")
                }

                getCategoryList()
                big_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if(big_category.getItemAtPosition(position).equals("큰 카테고리")){ }
                        for(i in 1..categoryParent.size-1){
                            categoryChild =  mutableListOf("작은 카테고리")

                            Log.d("카테고리 자식 ", categoryChild.toString())

                            setCategoryChild()

                            if(big_category.getItemAtPosition(position).equals(categoryParent[i])){
                                ServerUtil.retrofitService.requestCategoryChild(i)
                                    .enqueue(object : Callback<ArrayList<Category>> {
                                        override fun onResponse(call: Call<ArrayList<Category>>, response: Response<ArrayList<Category>>) {
                                            if (response.isSuccessful) {
                                                categoryListChild = response.body()!!
                                                for(i in 0..categoryListChild!!.size-1){
                                                    categoryChild?.add(categoryListChild!!.get(i).name)
                                                }
                                                Log.d(TAG, "카테고리 자식아이템리스트 받기 성공")

                                                setCategoryChild()

                                            }
                                        }
                                        override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                                            Log.d(TAG, "카테고리 자식아이템리스트 받기 실패")
                                            Toast.makeText(this@StudyModifyActivity, "카테고리 자식아이템리스트 받기 실패", Toast.LENGTH_LONG).show()
                                        }
                                    })

                            }
                        }

//                        categoryparentSeletedItem = categoryParent!![position]
//                        Log.d("categoryparentSeletedItem", categoryparentSeletedItem!!.toString())

                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }
                small_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if(small_category.getItemAtPosition(position).equals("작은 카테고리")) {

                        }
                        else{
                            categorychildSeletedItem = categoryChild!![position]
                            Log.d("categorychildSeletedItem", categorychildSeletedItem!!.toString())
                        }

                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

                //수정완료 누르면 정보들 다 받아와서 서버에 보내기
                modify_study_btn.setOnClickListener {
                    sendModifiedInfo(studyId)
                }
                //삭제하기 누르면 팝업창 -> 서버 통신
                delete_study.setOnClickListener {
                    var builder = AlertDialog.Builder(this)
                    builder.setTitle("알림")
                    builder.setMessage("해당 스터디를 삭제하시겠습니까?")
                    builder.setPositiveButton("탈퇴", DialogInterface.OnClickListener { dialog, which ->
                        sendDeleteStudy(accessToken,studyId)
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

    fun setStudyInfo(studyId : Int){
        ServerUtil.retrofitService.requestStudy(studyId)
            .enqueue(object : Callback<Study> {
                override fun onResponse(call: Call<Study>, response: Response<Study>) {
                    if (response.isSuccessful) {
                        studyInfo  = response.body()!!


                        if(studyInfo!!.image!!.profileImage != null){
                            Glide.with(this@StudyModifyActivity)
                                .load(studyInfo!!.image!!.profileImage)
                                .centerCrop()
                                .into(modify_study_cover_img)

                        } else{}


                        modify_study_name.setText(studyInfo!!.name)
                        people_num = studyInfo!!.numberOfPeople
                        modify_people_number.setText(people_num.toString())

                        tagArray = studyInfo!!.studyTags!!

                        //카테고리 정보 가져와서 spinner에 달기
                        seletedParent = studyInfo.parentCategory!!.name
                        seletedChild = studyInfo.childCategory!!.name

                        modify_introduce_text.setText(studyInfo.content)

                        //지역정보 가져와서 지도에 뿌리고 마커 찍기


                        if(studyInfo!!.online == true){
                            modify_check_online.isChecked = true
                        }else{modify_check_online.isChecked = false}

                        if(studyInfo!!.offline == true){
                            modify_check_offline.isChecked = true
                        }else{modify_check_offline.isChecked = false}


                        Log.d(TAG, "회원 지역정보 조회 성공")
                    }
                }
                override fun onFailure(call: Call<Study>, t: Throwable) {
                    Log.d(TAG, "회원 지역정보 조회 실패")
                    Toast.makeText(this@StudyModifyActivity, "회원 지역정보 조회 실패", Toast.LENGTH_LONG).show()
                }
            })
    }

    fun sendModifiedInfo(studyId : Int){

    }

    fun sendDeleteStudy(accessToken : String, studyId : Int){
        ServerUtil.retrofitService.requestDeleteStudy(accessToken,studyId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {

                        Log.d(TAG, "스터디 삭제 성공")

                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(TAG, "스터디 삭제 실패")
                    Toast.makeText(this@StudyModifyActivity, "스터디 삭제 실패", Toast.LENGTH_LONG).show()
                }
            })
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

    //카테고리 리스트 가져오기
    fun getCategoryList(){
        ServerUtil.retrofitService.requestCategoryParent()
            .enqueue(object : Callback<ArrayList<Category>> {
                override fun onResponse(call: Call<ArrayList<Category>>, response: Response<ArrayList<Category>>) {
                    if (response.isSuccessful) {
                        categoryListParent = response.body()
                        for(i in 0 .. categoryListParent!!.size-1){
                            categoryParent?.add(categoryListParent!!.get(i).name)

                        }
                        Log.d(TAG, "카테고리 부모아이템리스트 받기 성공")

                    }
                }
                override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                    Log.d(TAG, "카테고리 부모아이템리스트 받기 실패")
                    Toast.makeText(this@StudyModifyActivity, "카테고리 부모아이템리스트 받기 실패", Toast.LENGTH_LONG).show()
                }
            })

        spinnerAdapterparent = ArrayAdapter(this,R.layout.item_spinner, categoryParent)
        big_category.adapter = spinnerAdapterparent
        for(i in 0..categoryParent!!.size-1){
            if(seletedParent == categoryParent!![i]){
                big_category.setSelection(i)
            }
        }

    }
    //작은카테고리 셋팅
    private fun setCategoryChild(){
        Log.d("카테고리 자식 ", categoryChild.toString())
        spinnerAdapterchild = ArrayAdapter(this,R.layout.item_spinner, categoryChild)
        small_category.adapter = spinnerAdapterchild
        for(i in 0..categoryChild!!.size-1){
            if(seletedChild == categoryChild!![i]){
                small_category.setSelection(i)
            }
        }
    }

    //태그 추가
    fun addTag(chipGroup : ChipGroup, inflater: LayoutInflater): ArrayList<String> {
        var chip : Chip = inflater.inflate(R.layout.item_liketopic, chipGroup, false) as Chip

        tagArray.add(modify_edit_tag.text.toString())
        chip.text = modify_edit_tag.text.toString()
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

}