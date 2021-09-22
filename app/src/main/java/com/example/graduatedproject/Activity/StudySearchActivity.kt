package com.example.graduatedproject.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.StudySearchRecyclerAdapter
import com.example.graduatedproject.Fragment.SearchStudy
import com.example.graduatedproject.model.StudySearch
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_study_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudySearchActivity : AppCompatActivity() {
    private val TAG = StudySearchActivity::class.java.simpleName
    private var PAGE_NUM = 0 //현재페이지
    val LIST_LENGTH = 20 //리스트개수
    lateinit var adapter : StudySearchRecyclerAdapter

    var StudySearch : StudySearch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_search)

        val paramObject = JsonObject()


        val offline = intent.getBooleanExtra("offline", false)
        val online = intent.getBooleanExtra("online", false)
        val searchKeyword = intent.getStringExtra("searchKeyword")
        val categoryId = intent.getIntExtra("categoryId", 0)

        paramObject.addProperty("page", PAGE_NUM)
        paramObject.addProperty("size", LIST_LENGTH)
        paramObject.addProperty("offline", offline)
        paramObject.addProperty("online", online)
        paramObject.addProperty("searchKeyword", searchKeyword)
        paramObject.addProperty("categoryId", categoryId)

        applicationContext.apply {
            studysearch_recycler.layoutManager = LinearLayoutManager(applicationContext)
            adapter = StudySearchRecyclerAdapter(applicationContext)
            studysearch_recycler.adapter = adapter
        }

        loadList(paramObject)

        studysearch_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 끝에 도달했는지 확인
                if (!studysearch_recycler.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                    if(StudySearch!!.numberOfElements == LIST_LENGTH){
                        paramObject.addProperty("page",PAGE_NUM)
                        loadList(paramObject)
                    }
                    else{}
                }
            }
        })

        study_searchview.setOnClickListener {
            //Activity 변경
            val intent = Intent(this@StudySearchActivity, SearchStudy::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }

    }
    fun loadList(paramObject : JsonObject){
        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

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
                        StudySearch = response!!.body()

                        if(StudySearch!!.last == false){
                            if(PAGE_NUM != 0){ adapter.deleteLoading() }
                            adapter.setList(StudySearch!!.content)
                            // 새로운 게시물이 추가되었다는 것을 알려줌 (추가된 부분만 새로고침)
                            //새로운 값을 추가했으니 거기만 새로 그릴것을 요청
                            adapter.notifyDataSetChanged()
                            PAGE_NUM++
                        }
                        else{
                            if(StudySearch!!.numberOfElements != 0){
                                if(PAGE_NUM != 0){ adapter.deleteLoading() }
                                adapter.setList(StudySearch!!.content)
                                adapter.deleteLoading()
                                adapter.notifyDataSetChanged()
                                Toast.makeText(this@StudySearchActivity, "마지막페이지 입니다!", Toast.LENGTH_LONG).show()
                            }
                            else{}
                        }

                        Log.d(TAG, "검색결과리스트 받기 성공")
                    }
                }

                override fun onFailure(call: Call<StudySearch>, t: Throwable) {
                    Log.d(TAG, "검색결과리스트 받기 실패")
                    Toast.makeText(this@StudySearchActivity, "검색결과리스트 받기 실패", Toast.LENGTH_LONG).show()
                }
            })
    }
}