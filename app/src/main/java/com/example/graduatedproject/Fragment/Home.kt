package com.example.graduatedproject.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Activity.StudyCreateActivity
import com.example.graduatedproject.Adapter.HomeListAdapter
import com.example.graduatedproject.model.StudySearch
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Home : Fragment() {
    private val TAG = Home::class.java.simpleName
    lateinit var recyclerAdapter: HomeListAdapter
    private var PAGE_NUM = 0 //현재페이지
    val LIST_LENGTH = 20 //리스트개수
    val paramObject = JsonObject()
    var StudySearch : StudySearch? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home,container,false)


        val offline = true
        val online = true
        val searchKeyword : String = ""
        val categoryId = 4

        paramObject.addProperty("page", PAGE_NUM)
        paramObject.addProperty("size", LIST_LENGTH)
        paramObject.addProperty("offline", offline)
        paramObject.addProperty("online", online)
        paramObject.addProperty("searchKeyword", searchKeyword)
        paramObject.addProperty("categoryId", categoryId)

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

        val home_search_btn : LinearLayout = view.findViewById(R.id.home_search_btn)
        home_search_btn.setOnClickListener {
            var args : Bundle = Bundle()
            val dialog = SearchStudy().getInstance()
            dialog.setArguments(args)
            dialog.show(requireActivity().getSupportFragmentManager(),"tag")
        }

        val home_create_study : LinearLayout = view.findViewById(R.id.home_create_study)
        home_create_study.setOnClickListener {
            activity?.let {
                val intent = Intent(context, StudyCreateActivity::class.java)
                startActivity(intent)
            }
        }

        val home_recycler_view: RecyclerView = view.findViewById(R.id.home_recycler_view)
        home_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 끝에 도달했는지 확인
                if (!home_recycler_view.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                    if(StudySearch!!.numberOfElements == LIST_LENGTH){
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
        var accessToken : String? = pref.getString("access_token", "").toString()

        if(accessToken.equals("")){ accessToken = null }
        else { accessToken = "Bearer " + accessToken; }

        ServerUtil.retrofitService.requestStudySearch(
            accessToken,
            paramObject.get("page").asInt,
            paramObject.get("size").asInt,
            paramObject.get("offline").asBoolean,
            paramObject.get("online").asBoolean,
            paramObject.get("searchKeyword").asString,
            paramObject.get("categoryId").asInt)
            .enqueue(object : Callback<StudySearch> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<StudySearch>, response: Response<StudySearch>) {
                    if (response.isSuccessful) {
                        //응답값 다 받기
                        StudySearch = response.body()

                        if(StudySearch!!.last == false){
                            if(PAGE_NUM != 0){ recyclerAdapter.deleteLoading() }
                            recyclerAdapter.setList(StudySearch!!.content)
                            recyclerAdapter.notifyDataSetChanged()
                            PAGE_NUM++
                        }
                        else{
                            if(StudySearch!!.numberOfElements != 0){
                                if(PAGE_NUM != 0){ recyclerAdapter.deleteLoading() }
                                recyclerAdapter.setList(StudySearch!!.content)
                                recyclerAdapter.deleteLoading()
                                recyclerAdapter.notifyDataSetChanged()
                                Toast.makeText(getActivity(), "마지막페이지 입니다!", Toast.LENGTH_LONG).show()
                            }
                            else{}
                        }

                        Log.d(TAG, "검색결과리스트 받기 성공")
                    }
                }

                override fun onFailure(call: Call<StudySearch>, t: Throwable) {
                    Log.d(TAG, "검색결과리스트 받기 실패")
                    Toast.makeText(getActivity(), "검색결과리스트 받기 실패", Toast.LENGTH_LONG).show()
                }
            })
    }
}