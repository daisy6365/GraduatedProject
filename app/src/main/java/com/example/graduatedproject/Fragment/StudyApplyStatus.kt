package com.example.graduatedproject.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.StudyApplyStatusAdapter
import com.example.graduatedproject.Model.ApplyStudy
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyApplyStatus() : DialogFragment(){
    private var linearLayoutManager: RecyclerView.LayoutManager? = null
    private var recyclerAdapter: StudyApplyStatusAdapter? = null
    private val TAG = StudyApplyStatus::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_study_apply_status, container, false)
        // Inflate the layout for this fragment
        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestApplyStudy(accessToken)
            .enqueue(object : Callback<ArrayList<ApplyStudy>> {
                override fun onResponse(call: Call<ArrayList<ApplyStudy>>, response: Response<ArrayList<ApplyStudy>>) {
                    if (response.isSuccessful) {
                        val applyStudyList = response.body()!!

                        val recyclerView: RecyclerView = view.findViewById(R.id.apply_status_list)
                        recyclerAdapter = StudyApplyStatusAdapter(applyStudyList)
                        linearLayoutManager = LinearLayoutManager(activity)

                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.adapter = recyclerAdapter

                        Log.d(TAG, "회원 스터디 지원현황 정보 가져오기 성공")
                    }
                }
                override fun onFailure(call: Call<ArrayList<ApplyStudy>>, t: Throwable) {
                    Log.d(TAG, "회원 스터디 지원현황 정보 가져오기 실패")
                    Toast.makeText(getActivity(), "회원 스터디 지원현황 정보 가져오기 실패", Toast.LENGTH_SHORT).show()
                }
            })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apply_status_ok : TextView = view.findViewById(R.id.apply_status_ok)
        apply_status_ok.setOnClickListener {
            dismiss()
        }
    }

    fun getInstance(): StudyApplyStatus {
        return StudyApplyStatus()
    }
}