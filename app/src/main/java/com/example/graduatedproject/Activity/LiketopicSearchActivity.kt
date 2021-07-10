package com.example.graduatedproject.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.LikeSearchRecyclerAdapter
import com.example.graduatedproject.Model.ContentTag
import com.example.graduatedproject.Model.Likesearch
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_liketopic_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LiketopicSearchActivity : AppCompatActivity() {
    private val TAG = LiketopicSearchActivity::class.java.simpleName
    lateinit var adapter : LikeSearchRecyclerAdapter

    var PAGE_NUM = 0 //현재페이지
    val LIST_LENGTH = 20 //리스트개수
    var contentTag  =  ArrayList<ContentTag>()
    var tagSearch : Likesearch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liketopic_search)

        val paramObject = JsonObject()

        // 리사이클러뷰에 최초로 넣어줄 데이터를 load 한다
        // androidx.appcompat.widget.SearchView에 텍스트 치고 검색 -> 리스트 형태로 검색결과 나옴
        // infinite scroll -> 페이징 처리 10개씩
        liketopic_searchview.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            //쿼리텍스트가 제출됐을때 반응
            override fun onQueryTextSubmit(query: String?): Boolean {
                //쿼리텍스트 서버로 보내기
                PAGE_NUM = 0
                paramObject.addProperty("page", PAGE_NUM++)
                paramObject.addProperty("size", LIST_LENGTH)
                paramObject.addProperty("name", query.toString())
                liketopic_searchview.apply{
                    liketopicsearch_recycler.layoutManager = LinearLayoutManager(applicationContext)
                    adapter = LikeSearchRecyclerAdapter(context)
                    liketopicsearch_recycler.adapter = adapter
                }
                loadList(paramObject)
                return true
            }
            //쿼리텍스트가 변할때마다 반응
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


        //스크롤
        liketopicsearch_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 끝에 도달했는지 확인
                if (!liketopicsearch_recycler.canScrollVertically(1)) {
                    if(lastVisibleItemPosition == itemTotalCount){
                        adapter.deleteLoading()
                        paramObject.addProperty("page", PAGE_NUM++)
                        loadList(paramObject)
                    }
                }
            }
        })

    }

    fun loadList(paramObject : JsonObject){

        ServerUtil.retrofitService.requestLikesearch(paramObject.get("page").asInt,paramObject.get("size").asInt,paramObject.get("name").asString)
            .enqueue(object : Callback<Likesearch> {
                override fun onResponse(call: Call<Likesearch>, response: Response<Likesearch>) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "검색결과리스트 받기 성공")
                        //응답값 다 받기
                        tagSearch = response.body()

                        //setList 메서드를 이용해 새로 가져온 리스트들을 설정한다.
                        adapter.setList(tagSearch!!.content)

                        // 새로운 게시물이 추가되었다는 것을 알려줌 (추가된 부분만 새로고침)
                        //새로운 값을 추가했으니 거기만 새로 그릴것을 요청
                        adapter.notifyDataSetChanged()
                        //adapter.notifyItemRangeInserted((PAGE_NUM-1)*20, 20)
                    }
                }

                override fun onFailure(call: Call<Likesearch>, t: Throwable) {
                    Log.d(TAG, "검색결과리스트 받기 실패")
                    Toast.makeText(this@LiketopicSearchActivity, "검색결과리스트 받기 실패", Toast.LENGTH_LONG).show()
                }
            })
    }

}