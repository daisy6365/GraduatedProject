package com.example.graduatedproject.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.graduatedproject.Adapter.MemberListAdapter
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A fragment representing a list of Items.
 */
class StudyMember : Fragment() {
    private val TAG = StudyMember::class.java.simpleName
    var memberInfo =  ArrayList<Profile>()
    lateinit var memberListView : ListView
    lateinit var listAdapter : MemberListAdapter
    var studyId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_study_member, container, false)

        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestStudyMember(accessToken,studyId)
            .enqueue(object : Callback<ArrayList<Profile>> {
                override fun onResponse(call: Call<ArrayList<Profile>>, response: Response<ArrayList<Profile>>) {
                    if (response.isSuccessful) {
                        memberInfo = response.body()!!

                        Log.d(TAG, "스터디멤버 조회 성공")
                    }
                }

                override fun onFailure(call: Call<ArrayList<Profile>>, t: Throwable) {
                    Log.d(TAG, "스터디멤버 조회 실패")
                }
            })

        // Set the adapter
        memberListView = view.findViewById(R.id.member_list) as ListView
        listAdapter = MemberListAdapter(context,memberInfo,accessToken,studyId)

        memberListView.setAdapter(listAdapter)

        return view
    }

}