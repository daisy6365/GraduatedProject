package com.example.graduatedproject.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.graduatedproject.Adapter.MemberAddListAdapter
import com.example.graduatedproject.Adapter.MemberListAdapter
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyAddMember : Fragment() {
    private val TAG = StudyAddMember::class.java.simpleName
    var memberAddInfo =  ArrayList<Profile>()
    lateinit var memberAddListView : ListView
    lateinit var listAdapter : MemberAddListAdapter
    var studyId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_study_add_member, container, false)

        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestStudyAddMember(accessToken,studyId)
            .enqueue(object : Callback<ArrayList<Profile>> {
                override fun onResponse(call: Call<ArrayList<Profile>>, response: Response<ArrayList<Profile>>) {
                    if (response.isSuccessful) {
                        memberAddInfo = response.body()!!

                        Log.d(TAG, "스터디멤버 조회 성공")
                    }
                }

                override fun onFailure(call: Call<ArrayList<Profile>>, t: Throwable) {
                    Log.d(TAG, "스터디멤버 조회 실패")
                }
            })

        // Set the adapter
        memberAddListView = view.findViewById(R.id.member_add_list) as ListView
        listAdapter = MemberAddListAdapter(context,memberAddInfo,accessToken,studyId)

        memberAddListView.setAdapter(listAdapter)

        return view
    }

}