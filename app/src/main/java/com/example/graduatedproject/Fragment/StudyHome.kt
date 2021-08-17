package com.example.graduatedproject.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.graduatedproject.Activity.MainActivity
import com.example.graduatedproject.Model.Study
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import kotlinx.android.synthetic.main.fragment_study_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StudyHome(studyRoomId: Int) : Fragment() {
    private val TAG = StudyHome::class.java.simpleName
    var studyInfo = Study()
    var studyId : Int = studyRoomId
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_study_home, container, false)
        //화면 구성
        var bundle : Bundle? = getArguments()
        if(bundle == null){

            //스터디 정보 조회
            val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

            ServerUtil.retrofitService.requestStudy(accessToken, studyId)
                .enqueue(object : Callback<Study> {
                    override fun onResponse(call: Call<Study>, response: Response<Study>) {
                        if (response.isSuccessful) {
                            studyInfo = response.body()!!

                            //받은 스터디 정보들 화면에 뿌리기
                            if(studyInfo!!.image!!.profileImage != null){
                                Glide.with(view)
                                    .load(studyInfo!!.image!!.profileImage)
                                    .centerCrop()
                                    .into(study_cover_img)

                            } else{}

                            study_name.setText(studyInfo!!.name)
                            current_people_number.setText(studyInfo.currentNumberOfPeople.toString())
                            people_number.setText(studyInfo!!.numberOfPeople.toString())
                            big_category.setText(studyInfo!!.parentCategory!!.name)
                            small_category.setText(studyInfo!!.childCategory!!.name)
                            study_introduce_text.setText(studyInfo.content)
                            location_Info.setText(studyInfo.location!!.city + " " + studyInfo.location!!.gu + " " + studyInfo.location!!.dong)

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
                            if(studyInfo.online == true && studyInfo.offline == true){
                                study_on_off.setText("OFFLINE/ONLINE")
                            }
                            else if(studyInfo.online == true){
                                study_on_off.setText("ONLINE")
                            }
                            else{
                                study_on_off.setText("OFFLINE")
                            }
                            Log.d(TAG, "스터디 정보 받기 성공")
                        }
                    }
                    override fun onFailure(call: Call<Study>, t: Throwable) {
                        Log.d(TAG, "스터디 정보 받기 실패")
                        Toast.makeText(getActivity(), "스터디 정보 받기 실패", Toast.LENGTH_LONG).show()
                    }
                })
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val exit_study : LinearLayout = view.findViewById(R.id.exit_study)

        exit_study.setOnClickListener {

            var builder = AlertDialog.Builder(context)
            builder.setTitle("알림")
            builder.setMessage("해당 스터디를 탈퇴하시겠습니까?")
            builder.setPositiveButton("탈퇴", DialogInterface.OnClickListener { dialog, which ->
                val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
                var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

                ServerUtil.retrofitService.requestStudyDelete(accessToken,studyId)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {

                                Log.d(TAG, "스터디탈퇴 성공")
                                Toast.makeText(getActivity(), "스터디 탈퇴 되었습니다.", Toast.LENGTH_SHORT)
                                    .show();


                                //메인화면으로 전환
                                val intent = Intent(getActivity(), MainActivity::class.java)
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.d(TAG, "스터디탈퇴 실패")
                        }
                    })
            })
            builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                Log.d(TAG, "취소")
            })
            builder.show()
        }
    }
}