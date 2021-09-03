package com.example.graduatedproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()
        val studyId = intent.getIntExtra("studyId",0)

        Log.d("studyId", studyId.toString())
        if (intent.hasExtra("studyId")) {
            requestStudy(accessToken,studyId)
        }

        apply_study.setOnClickListener {
            //참가 신청
            ServerUtil.retrofitService.requestStudyApply(accessToken,studyId)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "스터디참가 신청 성공")
                            Toast.makeText(this@StudyApplyActivity, "스터디 참가신청을 했습니다.", Toast.LENGTH_LONG).show()

                            apply_study.visibility = View.GONE
                            apply_study_cancle.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d(TAG, "스터디참가 신청 실패")
                        Toast.makeText(this@StudyApplyActivity, "스터디참가 신청 실패", Toast.LENGTH_LONG).show()
                    }
                })

        }

        apply_study_cancle.setOnClickListener {
            //참가신청 취소
            ServerUtil.retrofitService.requestStudyApplyCancle(accessToken,studyId)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "스터디 참가신청 취소 성공")
                            Toast.makeText(this@StudyApplyActivity, "참가신청을 취소했습니다.", Toast.LENGTH_LONG).show()

                            apply_study_cancle.visibility = View.GONE
                            apply_study.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d(TAG, "스터디 참가신청 취소 실패")
                        Toast.makeText(this@StudyApplyActivity, "스터디 참가신청 취소 실패", Toast.LENGTH_LONG).show()
                    }
                })
        }

    }
    private fun requestStudy(accessToken : String, studyId : Int){

        ServerUtil.retrofitService.requestStudy(accessToken,studyId)
            .enqueue(object : Callback<Study> {
                override fun onResponse(call: Call<Study>, response: Response<Study>) {
                    if (response.isSuccessful) {
                        studyInfo  = response.body()!!

                        if(studyInfo?.image?.profileImage == null){
                            Glide.with(this@StudyApplyActivity)
                                .load(R.drawable.background_button)
                                .centerCrop()
                                .into(created_study_cover_img)

                        } else{
                            Glide.with(this@StudyApplyActivity)
                                .load(studyInfo!!.image!!.thumbnailImage)
                                .centerCrop()
                                .into(created_study_cover_img)
                        }

                        study_name.setText(studyInfo!!.name)
                        current_people_number.setText(studyInfo.currentNumberOfPeople.toString())
                        people_number.setText(studyInfo!!.numberOfPeople.toString())
                        big_category.setText(studyInfo!!.parentCategory!!.name)
                        small_category.setText(studyInfo!!.childCategory!!.name)

                        for(i in 0..studyInfo!!.studyTags!!.size-1){
                            when(i){
                                0 ->{
                                    study_tag1.text = studyInfo.studyTags!![0]
                                    study_tag1.visibility = View.VISIBLE
                                }
                                1 ->{
                                    study_tag2.text = studyInfo.studyTags!![1]
                                    study_tag2.visibility = View.VISIBLE
                                }
                                2 ->{
                                    study_tag3.text = studyInfo.studyTags!![2]
                                    study_tag3.visibility = View.VISIBLE
                                }
                                3 ->{
                                    study_tag4.text = studyInfo.studyTags!![3]
                                    study_tag4.visibility = View.VISIBLE
                                }
                                4 -> {
                                    study_tag5.text = studyInfo.studyTags!![4]
                                    study_tag5.visibility = View.VISIBLE
                                }
                                5 ->{
                                    study_tag6.text = studyInfo.studyTags!![5]
                                    study_tag6.visibility = View.VISIBLE
                                }
                            }
                        }

                        created_introduce_text.setText(studyInfo.content)
                        location_Info.setText(studyInfo.location!!.city + " " + studyInfo.location!!.gu + " " + studyInfo.location!!.dong)

                        if(studyInfo.online == true && studyInfo.offline == true){
                            study_apply_on_off.setText("OFFLINE/ONLINE")
                        }
                        else if(studyInfo.online == true){
                            study_apply_on_off.setText("ONLINE")
                        }
                        else{
                            study_apply_on_off.setText("OFFLINE")
                        }

                        if(studyInfo.status == "CLOSE"){
                            //상태 마감이라면 "지원하기"버튼 지우고
                            //마감 상태로 화면 출력
                            apply_study.visibility = View.GONE
                            closed_study.visibility = View.VISIBLE
                            closed_text.visibility = View.VISIBLE
                        }else{}//그외(OPEN)이라면 기존으로 두기


                        if(studyInfo.apply == true){
                            //지원한 상태 -> "지원하기" 버튼 지우고
                            //"지원 취소"버튼 띄우기
                            apply_study.visibility = View.GONE
                            apply_study_cancle.visibility = View.VISIBLE
                        }
                        else if(studyInfo.apply == null){
                            //이미 참가된 회원 -> 지원하기 버튼 지우기
                            apply_study.visibility = View.GONE
                        }
                        else{}//회원X,지원안한상태 -> 기존으로 두기

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