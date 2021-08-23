package com.example.graduatedproject.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.example.graduatedproject.Activity.StudyGroupCreateActivity
import com.example.graduatedproject.Adapter.StudyGroupListAdapter
import com.example.graduatedproject.Model.GroupList
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A fragment representing a list of Items.
 */
class StudyGroupList(studyRoomId: Int) : Fragment() {
    private var linearLayoutManager: RecyclerView.LayoutManager? = null
    private var recyclerAdapter: StudyGroupListAdapter? = null
    val TAG = StudyGroupList::class.java.simpleName
    var GroupListInfo : GroupList? = null
    var studyId : Int = studyRoomId
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_study_group_list, container, false)

        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        val home_recycler_view: RecyclerView = view.findViewById(R.id.home_recycler_view)
        val context = view.context
        context.apply {
            home_recycler_view.layoutManager = LinearLayoutManager(applicationContext)
            recyclerAdapter = HomeListAdapter(applicationContext)
            home_recycler_view.adapter = recyclerAdapter
        }

        loadList(paramObject)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val group_add_btn : LinearLayout = view.findViewById(R.id.group_add_btn)
        group_add_btn.setOnClickListener {
            activity?.let {
                val intent = Intent(context, StudyGroupCreateActivity::class.java)
                intent.putExtra("studyRoomId",studyId)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }

        val group_recycler: RecyclerView = view.findViewById(R.id.group_recycler)
        group_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 끝에 도달했는지 확인
                if (!group_recycler.canScrollVertically(1)) {
                    recyclerAdapter!!.deleteLoading()
                    if(lastVisibleItemPosition == itemTotalCount){
                        if(StudySearch!!.last == false){
                            paramObject.addProperty("page",PAGE_NUM)
                            loadList(paramObject)
                        }
                        else{}
                    }
                }
            }
        })
    }
    fun loadList(paramObject: JsonObject){
        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestGroupList(accessToken,studyId)
            .enqueue(object : Callback<GroupList> {
                override fun onResponse(call: Call<GroupList>, response: Response<GroupList>) {
                    if (response.isSuccessful) {
                        GroupListInfo = response.body()!!
                        Log.d(TAG, GroupListInfo.toString())

                        val recyclerView: RecyclerView = view.findViewById(R.id.chat_recycler)
                        recyclerAdapter = StudyGroupListAdapter(requireContext(),GroupListInfo!!.content)
                        linearLayoutManager = LinearLayoutManager(activity)

                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.adapter = recyclerAdapter


                        Log.d(TAG, "회원 스터디 정보 받기 성공")
                    }
                }
                override fun onFailure(call: Call<GroupList>, t: Throwable) {
                    Log.d(TAG, "회원 스터디 정보 받기 실패")
                    Toast.makeText(getActivity(), "회원 스터디 정보 받기 실패", Toast.LENGTH_LONG).show()
                }
            })

    }
}