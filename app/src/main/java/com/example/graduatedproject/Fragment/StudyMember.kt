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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.MemberListAdapter
import com.example.graduatedproject.Adapter.MyStudyListAdapter
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A fragment representing a list of Items.
 */
class StudyMember(studyRoomId: Int) : Fragment() {
    private var linearLayoutManager: RecyclerView.LayoutManager? = null
    private var recyclerAdapter: MemberListAdapter? = null
    private val TAG = StudyMember::class.java.simpleName
    var memberInfo =  ArrayList<Profile>()
    var studyId : Int = studyRoomId

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
                        val recyclerView: RecyclerView = view.findViewById(R.id.member_list)
                        recyclerAdapter = MemberListAdapter(requireContext(),memberInfo,accessToken,studyId)
                        linearLayoutManager = LinearLayoutManager(activity)

                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.adapter = recyclerAdapter

                    }
                }

                override fun onFailure(call: Call<ArrayList<Profile>>, t: Throwable) {
                    Log.d(TAG, "스터디멤버 조회 실패")
                }
            })


        return view
    }

}