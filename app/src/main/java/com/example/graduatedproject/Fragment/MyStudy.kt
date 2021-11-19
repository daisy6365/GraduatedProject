package com.example.graduatedproject.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.MyStudyListAdapter
import com.example.graduatedproject.model.Study
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyStudy : Fragment() {
    private var linearLayoutManager: RecyclerView.LayoutManager? = null
    private var recyclerAdapter: MyStudyListAdapter? = null
    private val TAG = StudyHome::class.java.simpleName
    var studyInfo : ArrayList<Study>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_my_study, container, false)

        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestMyStudy(accessToken)
            .enqueue(object : Callback<ArrayList<Study>> {
                override fun onResponse(call: Call<ArrayList<Study>>, response: Response<ArrayList<Study>>) {
                    if (response.isSuccessful) {
                        studyInfo = response.body()!!
                        Log.d(TAG, studyInfo.toString())

                        val recyclerView: RecyclerView = view.findViewById(R.id.mystudy_recycler)
                        recyclerAdapter = MyStudyListAdapter(requireContext(),studyInfo)
                        linearLayoutManager = LinearLayoutManager(activity)

                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.adapter = recyclerAdapter

                        Log.d(TAG, "회원 스터디 정보 받기 성공")
                    }
                }
                override fun onFailure(call: Call<ArrayList<Study>>, t: Throwable) {
                    Log.d(TAG, "회원 스터디 정보 받기 실패")
                    Toast.makeText(getActivity(), "회원 스터디 정보 받기 실패", Toast.LENGTH_LONG).show()
                }
            })
        return view
    }
}