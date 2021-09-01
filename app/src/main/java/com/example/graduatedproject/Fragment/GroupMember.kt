package com.example.graduatedproject.Fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.example.graduatedproject.Adapter.GroupMemberAdapter
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupMember(gatheringId : Int) : DialogFragment() {
    val groupId = gatheringId
    private var linearLayoutManager: RecyclerView.LayoutManager? = null
    private var recyclerAdapter: GroupMemberAdapter? = null
    private val TAG = GroupMember::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_member, container, false)
        // Inflate the layout for this fragment
        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestGroupUsers(accessToken,groupId)
            .enqueue(object : Callback<ArrayList<Profile>> {
                override fun onResponse(call: Call<ArrayList<Profile>>, response: Response<ArrayList<Profile>>) {
                    if (response.isSuccessful) {
                        val groupMemberInfo = response.body()!!

                        val recyclerView: RecyclerView = view.findViewById(R.id.group_member_list)
                        recyclerAdapter = GroupMemberAdapter(groupMemberInfo)
                        linearLayoutManager = LinearLayoutManager(activity)

                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.adapter = recyclerAdapter

                        Log.d(TAG, "모임참가자 조회 성공")

                    }
                }

                override fun onFailure(call: Call<ArrayList<Profile>>, t: Throwable) {
                    Log.d(TAG, "모임참가자 조회 실패")
                    Toast.makeText(getActivity(), "모임참가 신청 실패", Toast.LENGTH_LONG).show()
                }
            })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val group_member_ok : TextView = view.findViewById(R.id.group_member_ok)
        group_member_ok.setOnClickListener {
            dismiss()
        }
    }
}