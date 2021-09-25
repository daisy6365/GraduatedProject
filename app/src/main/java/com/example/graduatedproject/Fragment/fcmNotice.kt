package com.example.graduatedproject.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.NoticeRecyclerAdapter
import com.example.graduatedproject.R
import com.example.graduatedproject.databinding.FragmentFcmNoticeBinding
import com.example.graduatedproject.model.NotificationList
import com.example.graduatedproject.viewmodel.NoticeViewModel
import com.example.graduatedproject.Util.ServerUtil
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class fcmNotice : Fragment() {
    val TAG = fcmNotice::class.java.simpleName
    private lateinit var noticeviewModel : NoticeViewModel
    private lateinit var binding: FragmentFcmNoticeBinding
    private lateinit var recyclerAdapter: NoticeRecyclerAdapter
    var noticeListInfo : NotificationList? = null
    private var PAGE_NUM = 0 //현재페이지
    private val LIST_LENGTH = 20 //리스트개수
    val paramObject = JsonObject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFcmNoticeBinding.inflate(inflater,container,false)
        noticeviewModel = ViewModelProvider(this).get(NoticeViewModel::class.java)

        paramObject.addProperty("page", PAGE_NUM)
        paramObject.addProperty("size", LIST_LENGTH)

        binding.noticeRecycler.apply{
            binding.noticeRecycler.layoutManager = LinearLayoutManager(context)
            recyclerAdapter = NoticeRecyclerAdapter()
            binding.noticeRecycler.adapter = recyclerAdapter
        }

        loadList(paramObject)

        noticeviewModel.noticeListInfo.observe(viewLifecycleOwner, Observer {
            if(it != null){
                if(noticeviewModel.noticeListInfo.value!!.last == false){
                    if(PAGE_NUM != 0){ recyclerAdapter.deleteLoading() }
                    recyclerAdapter.setList(noticeviewModel.noticeListInfo.value!!.content)
                    // 새로운 게시물이 추가되었다는 것을 알려줌 (추가된 부분만 새로고침)
                    //새로운 값을 추가했으니 거기만 새로 그릴것을 요청
                    recyclerAdapter.notifyDataSetChanged()
                    PAGE_NUM++
                }
                else{
                    if(noticeviewModel.noticeListInfo.value!!.numberOfElements != 0){
                        if(PAGE_NUM != 0){ recyclerAdapter.deleteLoading() }
                        recyclerAdapter.setList(noticeviewModel.noticeListInfo.value!!.content)
                        recyclerAdapter.deleteLoading()
                        recyclerAdapter.notifyDataSetChanged()
                        Toast.makeText(getActivity(), "마지막페이지 입니다!", Toast.LENGTH_LONG).show()
                    }
                    else{}
                }
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noticeRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 끝에 도달했는지 확인
                if (!binding.noticeRecycler.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                    if(noticeviewModel.noticeListInfo.value!!.numberOfElements == LIST_LENGTH){
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

        ServerUtil.retrofitService.requestNotification(
            accessToken,
            paramObject.get("page").asInt,
            paramObject.get("size").asInt).enqueue(object : Callback<NotificationList> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<NotificationList>, response: Response<NotificationList>) {
                if (response.isSuccessful) {
                    noticeListInfo = response.body()!!
                    Log.d(TAG, noticeListInfo.toString())

                    //뷰모델에 값 넣기
                    noticeviewModel.setData(noticeListInfo!!)

                    Log.d(TAG, "회원 알림정보 받기 성공")
                }
            }
            override fun onFailure(call: Call<NotificationList>, t: Throwable) {
                Log.d(TAG, "회원 알림정보 정보 받기 실패")
                Toast.makeText(getActivity(), "회원 알림정보 정보 받기 실패", Toast.LENGTH_LONG).show()
            }
        })

    }

}