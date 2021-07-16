package com.example.graduatedproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.graduatedproject.Model.Study
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import kotlinx.android.synthetic.main.activity_study_apply.*
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyApplyActivity : AppCompatActivity() {
    private val TAG = StudyApplyActivity::class.java.simpleName
    var studyInfo = Study()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_apply)

        if (intent.hasExtra("studyId")) {
            requestStudy()
        }
    }
    private fun requestStudy(){
        val studyId = intent.getIntExtra("studyId",0)

        ServerUtil.retrofitService.requestStudy(studyId)
            .enqueue(object : Callback<Study> {
                override fun onResponse(call: Call<Study>, response: Response<Study>) {
                    if (response.isSuccessful) {
                        studyInfo  = response.body()!!
                        Log.d("location 회원 지역정보 조회", response.body().toString())

                        Glide.with(view)
                            .load(studyInfo!!.image.profileImage)
                            .into(created_study_cover_img)

                        study_name.setText(studyInfo!!.name)
                        people_number.setText(studyInfo!!.currentNumberOfPeople)
                        big_category.setText(studyInfo!!.parentCategory.name)
                        small_category.setText(studyInfo!!.childCategory.name)
                        created_introduce_text.setText(studyInfo.content)


                        Log.d(TAG, "회원 지역정보 조회 성공")
                    }
                }
                override fun onFailure(call: Call<Study>, t: Throwable) {
                    Log.d(TAG, "회원 지역정보 조회 실패")
                    Toast.makeText(this@StudyApplyActivity, "회원 지역정보 조회 실패", Toast.LENGTH_LONG).show()
                }
            })
    }
}