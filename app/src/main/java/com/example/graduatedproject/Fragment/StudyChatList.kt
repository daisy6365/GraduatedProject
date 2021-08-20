package com.example.graduatedproject.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Adapter.StudyChatListAdapter
import com.example.graduatedproject.Model.ChatRoom
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudyChat(studyRoomId: Int) : Fragment() {
    private var linearLayoutManager: RecyclerView.LayoutManager? = null
    private var recyclerAdapter: StudyChatListAdapter? = null
    private val TAG = StudyChat::class.java.simpleName
    var chatRoomInfo : ArrayList<ChatRoom>? = null
    var studyId = studyRoomId


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_study_chat, container, false)

        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestChatRoom(accessToken,studyId)
            .enqueue(object : Callback<ArrayList<ChatRoom>> {
                override fun onResponse(call: Call<ArrayList<ChatRoom>>, response: Response<ArrayList<ChatRoom>>) {
                    if (response.isSuccessful) {
                        chatRoomInfo = response.body()!!
                        Log.d(TAG, chatRoomInfo.toString())

                        val recyclerView: RecyclerView = view.findViewById(R.id.chat_recycler)
                        recyclerAdapter = StudyChatListAdapter(requireContext(),chatRoomInfo)
                        linearLayoutManager = LinearLayoutManager(activity)

                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.adapter = recyclerAdapter


                        Log.d(TAG, "회원 스터디 정보 받기 성공")
                    }
                }
                override fun onFailure(call: Call<ArrayList<ChatRoom>>, t: Throwable) {
                    Log.d(TAG, "회원 스터디 정보 받기 실패")
                    Toast.makeText(getActivity(), "회원 스터디 정보 받기 실패", Toast.LENGTH_LONG).show()
                }
            })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //채팅방 생성
        val chatroom_add_btn : LinearLayout = view.findViewById(R.id.chatroom_add_btn)
        chatroom_add_btn.setOnClickListener {
            //팝업창을 띄움
        }

        //아이템 메뉴 (수정, 삭제)

    }
}