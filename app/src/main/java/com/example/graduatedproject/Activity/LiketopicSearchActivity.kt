package com.example.graduatedproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.SearchRecyclerAdapter
import com.example.graduatedproject.Model.Likesearch
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_liketopic_search.*
import kotlinx.android.synthetic.main.activity_liketopic_search.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiketopicSearchActivity : AppCompatActivity() {
    private val TAG = LiketopicSearchActivity::class.java.simpleName
    private lateinit var adapter : SearchRecyclerAdapter

    var PAGE_NUM = 0 //현재페이지
    val LIST_LENGTH = 10 //총개수
    private var totalCount = 0 // 전체 아이템 개수
    private var isLast = false // 마지막페이지인지 여부


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liketopic_search)

        liketopicsearch_recycler.adapter = adapter


        // 리사이클러뷰에 최초로 넣어줄 데이터를 load 한다
        // androidx.appcompat.widget.SearchView에 텍스트 치고 검색 -> 리스트 형태로 검색결과 나옴
        // infinite scroll -> 페이징 처리 10개씩
        liketopic_searchview.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            //쿼리텍스트가 제출됐을때 반응
            override fun onQueryTextSubmit(query: String?): Boolean {
                //쿼리텍스트 서버로 보내기
                val paramObject = JsonObject()
                paramObject.addProperty("name", query.toString())
                paramObject.addProperty("page", PAGE_NUM++)
                paramObject.addProperty("size", LIST_LENGTH)

                loadList(paramObject)
                initScrollListener(paramObject)

                return true
            }

            //쿼리텍스트가 변할때마다 반응
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
    private fun loadList(paramObject:JsonObject) {
        //검색결과 리스트 받기
        ServerUtil.retrofitService.requestLikesearch(paramObject)
            .enqueue(object : Callback<Likesearch> {
                override fun onResponse(call: Call<Likesearch>, response: Response<Likesearch>) {
                    if (response.isSuccessful) {
                        //응답값 다 받기
                        var Likesearch = response.body()
                        if (Likesearch != null) {
                            totalCount = Likesearch.totalElements
                            isLast = Likesearch.last

                            //Adapter 실행
                            adapter = SearchRecyclerAdapter(Likesearch, LIST_LENGTH, LayoutInflater.from(this@LiketopicSearchActivity))
                        }

                        Log.d(TAG, "검색결과리스트 받기 최초성공")
                        // 받은 관심주제리스트 ListView에 item을 통해 화면에 출력
                        // 리스트 0~29까지 훝으면서 item에 값을 저장한뒤 ListView에 하나씩 붙이기

                        finish()
                    }
                }

                override fun onFailure(call: Call<Likesearch>, t: Throwable) {
                    Log.d(TAG, "검색결과리스트 받기 실패")
                    Toast.makeText(this@LiketopicSearchActivity, "검색결과리스트 받기 최초실패", Toast.LENGTH_LONG).show()
                }
            })
    }

    // 리사이클러뷰에 더 보여줄 데이터를 로드하는 경우
    private fun loadMorePosts(paramObject:JsonObject) {
        // 너무 빨리 데이터가 로드되면 스크롤 되는 Ui 를 확인하기 어려우므로,
        // Handler 를 사용하여 1초간 postDelayed 시켰다
        val handler = android.os.Handler()
        handler.postDelayed({
            ServerUtil.retrofitService.requestLikesearch(paramObject)
                .enqueue(object : Callback<Likesearch> {
                    override fun onResponse(call: Call<Likesearch>, response: Response<Likesearch>) {
                        if (response.isSuccessful) {
                            //응답값 다 받기
                            var Likesearch = response.body()
                            if (Likesearch != null) {
                                totalCount = Likesearch.totalElements
                                isLast = Likesearch.last

                            }
                        } else {
                            Log.d(TAG, "검색결과리스트 받기 통신 도중 실패")
                        }
                    }

                    override fun onFailure(call: Call<Likesearch>, t: Throwable) {
                        Log.d(TAG, "검색결과리스트 받기 통신실패")
                    }
                })
        }, 1000)
    }


    private fun initScrollListener(paramObject:JsonObject) {
        liketopicsearch_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = liketopicsearch_recycler.layoutManager

                // hasNextPage() -> 마지막 페이지가 아닌경우
                // 다음페이지가 있는경우
                if (isLast == false) {
                    val lastVisibleItem = (layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()

                    // 마지막으로 보여진 아이템 position 이
                    // 전체 아이템 개수보다 5개 모자란 경우, 데이터를 loadMore 한다
                    if (layoutManager.itemCount <= lastVisibleItem + 5) {
                        loadMorePosts(paramObject)
                        isLast = true
                    }
                }
            }
        })
    }

}