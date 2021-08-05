package com.example.graduatedproject.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.graduatedproject.Activity.StudyMemberActivity
import com.example.graduatedproject.Activity.StudyModifyActivity
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.AlertDialog
import android.content.DialogInterface

class StudyBuild : Fragment() {
    val TAG = StudyBuild::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_study_build, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val member_build : LinearLayout = view.findViewById(R.id.member_build)
        val study_modify : LinearLayout = view.findViewById(R.id.study_modify)

        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        member_build.setOnClickListener {
            activity?.let {
                    val intent = Intent(context, StudyMemberActivity::class.java)
                    startActivity(intent)
                }
        //            var userInfo :Profile? = null
//            ServerUtil.retrofitService.requestProfile(accessToken)
//                .enqueue(object : Callback<Profile> {
//                    override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
//                        if (response.isSuccessful) {
//                            userInfo = response.body()
//                            Log.d(TAG, "프로필 받기 성공")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<Profile>, t: Throwable) {
//                        Log.d(TAG, "프로필 받기 실패")
//                    }
//                })
//            if(userInfo!!.role == "ADMIN"){
//                //스터디 호스트라면
//                activity?.let {
//                    val intent = Intent(context, StudyMemberActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//            else if(userInfo!!.role == "USER"){
//                var builder = AlertDialog.Builder(context)
//                builder.setTitle("경고")
//                builder.setMessage("관리자만 접근이 가능합니다.")
//                builder.setNegativeButton("확인", DialogInterface.OnClickListener { dialog, which ->
//                    Log.d(TAG, "확인")
//                })
//            }
        }

        study_modify.setOnClickListener {

            activity?.let {
                    val intent = Intent(context, StudyModifyActivity::class.java)
                    startActivity(intent)
                }

        //            var userInfo :Profile? = null
//
//            ServerUtil.retrofitService.requestProfile(accessToken)
//                .enqueue(object : Callback<Profile> {
//                    override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
//                        if (response.isSuccessful) {
//                            userInfo = response.body()
//                            Log.d(TAG, "프로필 받기 성공")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<Profile>, t: Throwable) {
//                        Log.d(TAG, "프로필 받기 실패")
//                    }
//                })
//            if(userInfo!!.role == "ADMIN"){
//                activity?.let {
//                    val intent = Intent(context, StudyModifyActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//            else if(userInfo!!.role == "USER"){
//                var builder = AlertDialog.Builder(context)
//                builder.setTitle("경고")
//                builder.setMessage("관리자만 접근이 가능합니다.")
//                builder.setNegativeButton("확인", DialogInterface.OnClickListener { dialog, which ->
//                    Log.d(TAG, "확인")
//                })
//            }

        }
    }
}