package com.example.graduatedproject.Activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.MessageRecyclerAdpater
import com.example.graduatedproject.model.ChatList
import com.example.graduatedproject.model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.databinding.ActivityChatBinding
import com.example.graduatedproject.viewmodel.StompViewModel
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.graduatedproject.Util.ServerUtil
import com.example.graduatedproject.viewmodel.MessageViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class StudyChatActivity : AppCompatActivity() {
    val TAG = StudyChatActivity::class.java.simpleName
    private lateinit var messageViewModel : MessageViewModel
    private val stompViewModel: StompViewModel by viewModels()
    private lateinit var binding: ActivityChatBinding
    private lateinit var recyclerAdapter: MessageRecyclerAdpater
    var chatListInfo : ChatList? = null
    private var PAGE_NUM = 0 //현재페이지
    private val LIST_LENGTH = 20 //리스트개수
    val paramObject = JsonObject()
    var userId : Int? = null
    lateinit var sendmessage : String
    private lateinit var currentTime : String

    companion object{
        val MY = 1
        val OTHER = 2
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat)

        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String =  pref.getString("access_token", "").toString()
        val studyChatId = intent.getIntExtra("studyChatId",0)

        messageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        stompViewModel.connectStomp(studyChatId, accessToken)
        val long_now = System.currentTimeMillis()
        val t_date = Date(long_now)
        val t_dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("ko", "KR"))
        currentTime = t_dateFormat.format(t_date)
        Log.d(TAG, currentTime.toString())

        //바인딩 초기화
        binding.apply {
            viewmodel = stompViewModel
            lifecycleOwner = this@StudyChatActivity
        }
        getUserId(accessToken)

        paramObject.addProperty("page", PAGE_NUM)
        paramObject.addProperty("size", LIST_LENGTH)
        paramObject.addProperty("lastMessageDate", currentTime)

        //채팅방ID에 따른 채팅메세지들 조회해오기
        loadList(accessToken, studyChatId, paramObject)

        stompViewModel.message.observe(this, Observer {
            if(it != null) {
                recyclerAdapter.add(it)
                //binding.chatRecycler.smoothScrollToPosition(binding.chatRecycler.adapter!!.itemCount)
            }
        })

        //채팅조회시 (채팅리스트) 뷰모델 관찰 -> 무한 스크롤
        messageViewModel.messageInfo.observe(this, Observer { it ->
            if(it != null) {
                if (messageViewModel.messageInfo.value!!.last == false) {
                    recyclerAdapter.setList(messageViewModel.messageInfo.value!!.content)
                    // 새로운 게시물이 추가되었다는 것을 알려줌 (추가된 부분만 새로고침)
                    //새로운 값을 추가했으니 거기만 새로 그릴것을 요청
                    recyclerAdapter.notifyDataSetChanged()
                    if(PAGE_NUM == 0){
                        binding.chatRecycler.smoothScrollToPosition(binding.chatRecycler.adapter!!.itemCount)
                    }
                    PAGE_NUM++
                }
                else{
                    if(messageViewModel.messageInfo.value!!.numberOfElements != 0){
                        recyclerAdapter.setList(messageViewModel.messageInfo.value!!.content)
                        recyclerAdapter.notifyDataSetChanged()
                        Toast.makeText(this, "마지막페이지 입니다!", Toast.LENGTH_LONG).show()
                    }
                    else{}

                }
            }
        })

        binding.chatRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 최상단에 도달했는지 확인
                if (!binding.chatRecycler.canScrollVertically(-1)) {
                    //30개라면 페이징함
                    if(messageViewModel.messageInfo.value!!.numberOfElements == LIST_LENGTH){
                        paramObject.addProperty("page",PAGE_NUM)
                        loadList(accessToken, studyChatId, paramObject)
                    }
                }
            }
        })

        binding.sendBtn.setOnClickListener {
            sendmessage = binding.chatEdit.text.toString()

            if (!TextUtils.isEmpty(sendmessage)) {
                sendMessage(sendmessage, studyChatId, accessToken)
                binding.chatEdit.text.clear()
            } else {
                Toast.makeText(this, "메세지를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadList(accessToken : String, studyChatId : Int, paramObject: JsonObject) {
        ServerUtil.retrofitService.requestChat(
            "Bearer " + accessToken,
            studyChatId,
            paramObject.get("page").asInt,
            paramObject.get("size").asInt,
            paramObject.get("lastMessageDate").asString
        ).enqueue(object : Callback<ChatList> {
            override fun onResponse(call: Call<ChatList>, response: Response<ChatList>) {
                if (response.isSuccessful) {
                    chatListInfo = response.body()!!
                    Log.d(TAG, chatListInfo.toString())
                    //뷰모델에 값 넣기

                    messageViewModel.setData(chatListInfo!!)
                    Log.d(TAG, "채팅메세지 조회 성공")
                }
            }
            override fun onFailure(call: Call<ChatList>, t: Throwable) {
                Log.d(TAG, "채팅메세지 조회 실패")
                Toast.makeText(this@StudyChatActivity, "채팅메세지 조회 실패", Toast.LENGTH_LONG).show()
            }
        })
    }


    fun getUserId(accessToken: String){
        ServerUtil.retrofitService.requestProfile("Bearer " + accessToken)
            .enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        val profile = response.body()!!
                        userId = profile.id
                        Log.d(TAG, userId.toString())

                        binding.chatRecycler.apply{
                            binding.chatRecycler.layoutManager = LinearLayoutManager(context)
                            recyclerAdapter = MessageRecyclerAdpater(userId)
                            binding.chatRecycler.adapter = recyclerAdapter
                        }
                    }
                }
                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.d(TAG, "프로필 받기 실패")
                }
            })
    }

    fun sendMessage(message: String, studyChatId: Int, accessToken: String) {
        stompViewModel.sendMessage(message, studyChatId, accessToken)
    }

    override fun onBackPressed() {
        stompViewModel.disconnectStomp()
        super.onBackPressed()
    }
}