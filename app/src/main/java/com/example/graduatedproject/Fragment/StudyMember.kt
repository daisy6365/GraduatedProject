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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.MemberListAdapter
import com.example.graduatedproject.Adapter.MyStudyListAdapter
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.example.graduatedproject.databinding.FragmentStudyMemberBinding
import com.example.graduatedproject.viewmodel.MemberListViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A fragment representing a list of Items.
 */
class StudyMember(studyRoomId: Int) : Fragment() {
    private lateinit var binding : FragmentStudyMemberBinding
    private lateinit var viewmodel : MemberListViewModel
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
        binding = FragmentStudyMemberBinding.inflate(inflater,container,false)
        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestStudyMember(accessToken,studyId)
            .enqueue(object : Callback<ArrayList<Profile>> {
                override fun onResponse(call: Call<ArrayList<Profile>>, response: Response<ArrayList<Profile>>) {
                    if (response.isSuccessful) {
                        memberInfo = response.body()!!
                        viewmodel.setData(memberInfo)

                        Log.d(TAG, "스터디멤버 조회 성공")
                        binding.memberList.layoutManager = LinearLayoutManager(activity)
                    }
                }

                override fun onFailure(call: Call<ArrayList<Profile>>, t: Throwable) {
                    Log.d(TAG, "스터디멤버 조회 실패")
                }
            })
        viewmodel = ViewModelProvider(requireActivity()).get(MemberListViewModel::class.java)
        viewmodel.memberListInfo.observe(viewLifecycleOwner, Observer {
            if(it != null){
                recyclerAdapter = MemberListAdapter(requireContext(),memberInfo,accessToken,studyId)
                binding.memberList.adapter = recyclerAdapter
            }
        })


        return binding.root
    }

}