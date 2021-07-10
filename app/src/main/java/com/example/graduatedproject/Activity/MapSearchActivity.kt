package com.example.graduatedproject.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.LikeSearchRecyclerAdapter
import com.example.graduatedproject.Adapter.MapSearchRecyclerAdapter
import com.example.graduatedproject.Model.ContentLocation
import com.example.graduatedproject.Model.Likesearch
import com.example.graduatedproject.Model.LocationSearch
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_liketopic_search.*
import kotlinx.android.synthetic.main.activity_map_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.MotionEvent

import android.view.View.OnTouchListener




class MapSearchActivity : AppCompatActivity() {
    private val TAG = MapSearchActivity::class.java.simpleName
    lateinit var adapter : MapSearchRecyclerAdapter

    private var PAGE_NUM = 0 //현재페이지
    val LIST_LENGTH = 20 //리스트개수
    var contentLocation  =  ArrayList<ContentLocation>()
    var locationSearch : LocationSearch? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_search)

        val paramObject = JsonObject()


        place_searchview.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            //쿼리텍스트가 제출됐을때 반응
            override fun onQueryTextSubmit(query: String?): Boolean {
                //쿼리텍스트 서버로 보내기
                PAGE_NUM = 0
                paramObject.addProperty("page", PAGE_NUM++)
                paramObject.addProperty("size", LIST_LENGTH)
                paramObject.addProperty("searchName", query.toString())

                place_searchview.apply {
                    placesearch_recycler.layoutManager = LinearLayoutManager(applicationContext)
                    adapter = MapSearchRecyclerAdapter(context)
                    placesearch_recycler.adapter = adapter
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
        placesearch_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 끝에 도달했는지 확인
                if (!placesearch_recycler.canScrollVertically(1)) {
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
        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()


        ServerUtil.retrofitService
            .requestLocationsearch(accessToken, paramObject.get("page").asInt,paramObject.get("size").asInt,paramObject.get("searchName").asString)
            .enqueue(object : Callback<LocationSearch> {
                override fun onResponse(call: Call<LocationSearch>, response: Response<LocationSearch>) {
                    if (response.isSuccessful) {

                        //응답값 다 받기
                        locationSearch = response!!.body()
                        adapter.setList(locationSearch!!.content)

                        Log.d(TAG, "검색결과리스트 받기 성공")

                        // 새로운 게시물이 추가되었다는 것을 알려줌 (추가된 부분만 새로고침)
                        //새로운 값을 추가했으니 거기만 새로 그릴것을 요청
//                        if(PAGE_NUM > 0){
//
//                        }
                        adapter.notifyDataSetChanged()
                        //adapter.notifyItemRangeInserted((PAGE_NUM-1)*20, 20)
                    }
                }

                override fun onFailure(call: Call<LocationSearch>, t: Throwable) {
                    Log.d(TAG, "검색결과리스트 받기 실패")
                    Toast.makeText(this@MapSearchActivity, "검색결과리스트 받기 실패", Toast.LENGTH_LONG).show()
                }
            })
    }
}