package com.example.graduatedproject.Fragment

import android.annotation.SuppressLint
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.graduatedproject.Activity.StudyGroupCreateActivity
import com.example.graduatedproject.Adapter.StudyGroupListAdapter
import com.example.graduatedproject.Model.GroupList
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.example.graduatedproject.databinding.FragmentStudyGroupListBinding
import com.example.graduatedproject.viewmodel.GroupListViewModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_study_group_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyGroupList(studyRoomId: Int) : Fragment() {
    private lateinit var binding: FragmentStudyGroupListBinding
    private lateinit var viewmodel : GroupListViewModel
    private val TAG = StudyGroupList::class.java.simpleName
    lateinit var recyclerAdapter: StudyGroupListAdapter
    private var PAGE_NUM = 0 //현재페이지
    val LIST_LENGTH = 20 //리스트개수
    val paramObject = JsonObject()

    var GroupListInfo : GroupList? = null
    var studyId : Int = studyRoomId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudyGroupListBinding.inflate(inflater,container,false)
        //viewmodel = ViewModelProvider(requireActivity()).get(GroupListViewModel::class.java)

        paramObject.addProperty("page", PAGE_NUM)
        paramObject.addProperty("size", LIST_LENGTH)

//        val group_recycler: RecyclerView = requireView().findViewById(R.id.group_recycler)
//        val context = requireView().context
//        context.apply {
//            group_recycler.layoutManager = LinearLayoutManager(applicationContext)
//            recyclerAdapter = StudyGroupListAdapter(applicationContext)
//            group_recycler.adapter = recyclerAdapter
//        }

//        binding.groupRecycler.apply{
//            binding.groupRecycler.layoutManager = LinearLayoutManager(context)
//            recyclerAdapter = StudyGroupListAdapter(context)
//            binding.groupRecycler.adapter = recyclerAdapter
//        }
//
//        loadList(paramObject)
//
//        viewmodel.groupListInfo.observe(viewLifecycleOwner, Observer {
//            if(it != null){
//                if(viewmodel.groupListInfo.value!!.last == false){
//                    if(PAGE_NUM != 0){ recyclerAdapter.deleteLoading() }
//                    recyclerAdapter.setList(viewmodel.groupListInfo.value!!.content)
//                    // 새로운 게시물이 추가되었다는 것을 알려줌 (추가된 부분만 새로고침)
//                    //새로운 값을 추가했으니 거기만 새로 그릴것을 요청
//                    recyclerAdapter.notifyDataSetChanged()
//                    PAGE_NUM++
//                }
//                else{
//                    if(viewmodel.groupListInfo.value!!.numberOfElements != 0){
//                        if(PAGE_NUM != 0){ recyclerAdapter.deleteLoading() }
//                        recyclerAdapter.setList(viewmodel.groupListInfo.value!!.content)
//                        recyclerAdapter.deleteLoading()
//                        recyclerAdapter.notifyDataSetChanged()
//                        Toast.makeText(getActivity(), "마지막페이지 입니다!", Toast.LENGTH_LONG).show()
//                    }
//                    else{}
//                }
//            }
//        })

        return binding.root
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
                if (!group_recycler.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                    if(viewmodel.groupListInfo.value!!.numberOfElements == LIST_LENGTH){
                        paramObject.addProperty("page",PAGE_NUM)
                        loadList(paramObject)
                    }
                    else{}
                }
            }
        })
    }
    fun loadList(paramObject: JsonObject){
        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestGroupList(
            accessToken,
            studyId,
            paramObject.get("page").asInt,
            paramObject.get("size").asInt).enqueue(object : Callback<GroupList> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<GroupList>, response: Response<GroupList>) {
                    if (response.isSuccessful) {
                        GroupListInfo = response.body()!!
                        Log.d(TAG, GroupListInfo.toString())

                        //뷰모델에 값 넣기
                        viewmodel.setData(GroupListInfo!!)

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