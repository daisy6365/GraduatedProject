package com.example.graduatedproject.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.SearchRecyclerAdapter
import com.example.graduatedproject.Model.Content
import com.example.graduatedproject.Model.Likelist
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
    lateinit var adapter : SearchRecyclerAdapter

    var PAGE_NUM = 0 //현재페이지
    val LIST_LENGTH = 10 //리스트개수

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
                if (!liketopicsearch_recycler.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                    paramObject.addProperty("page",PAGE_NUM++)
                    adapter.deleteLoading()
                    loadList(paramObject)
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
                        var Likesearch = response.body()

                        liketopicsearch_recycler.layoutManager = LinearLayoutManager(applicationContext)
                        adapter = SearchRecyclerAdapter(Likesearch)
                        liketopicsearch_recycler.adapter = adapter
                        //setList 메서드를 이용해 새로 가져온 리스트들을 설정한다.
                        adapter.setList(Likesearch!!.content)

                        // 새로운 게시물이 추가되었다는 것을 알려줌 (추가된 부분만 새로고침)
                        //새로운 값을 추가했으니 거기만 새로 그릴것을 요청
                        adapter.notifyItemRangeInserted((PAGE_NUM) * LIST_LENGTH, LIST_LENGTH)


                        var pref = getSharedPreferences("login_sp", AppCompatActivity.MODE_PRIVATE)
                        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

                        if (intent.hasExtra("add_item"))
                        {
                            val new_addtag = intent.getStringExtra("add_item")
                            for (i in 0 .. Likesearch.size-1){
                                if(Likesearch!!.content[i].name == new_addtag){
                                    val new_addtagId = Likesearch!!.content[i].id


                                    ServerUtil.retrofitService.requestLikeadd(accessToken,new_addtagId)
                                        .enqueue(object : Callback<Void> {
                                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                                if (response.isSuccessful) {
                                                    Log.d(TAG, "관심태그추가 성공")
                                                }
                                                val intent = Intent(this@LiketopicSearchActivity, LiketopicActivity::class.java)
                                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                                finish()
                                            }

                                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                                Log.d(TAG, "관심태그추가 실패")
                                                Toast.makeText(this@LiketopicSearchActivity, "관심태그추가 실패", Toast.LENGTH_LONG).show()
                                            }
                                        })
                                }

                            }
                        }
                        else { }
                    }
                }

                override fun onFailure(call: Call<Likesearch>, t: Throwable) {
                    Log.d(TAG, "검색결과리스트 받기 실패")
                    Toast.makeText(this@LiketopicSearchActivity, "검색결과리스트 받기 실패", Toast.LENGTH_LONG).show()
                }
            })
    }

}