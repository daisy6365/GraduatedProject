package com.example.graduatedproject.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Activity.StudyChatActivity
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
                        //registerForContextMenu(recyclerView)


                        Log.d(TAG, "채팅방 리스트 정보 받기 성공")
                    }
                }
                override fun onFailure(call: Call<ArrayList<ChatRoom>>, t: Throwable) {
                    Log.d(TAG, "채팅방 리스트 정보 받기 실패")
                    Toast.makeText(getActivity(), "채팅방 리스트 정보 받기 실패", Toast.LENGTH_LONG).show()
                }
            })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //채팅방 생성
        val chatroom_add_btn : LinearLayout = view.findViewById(R.id.chatroom_add_btn)
        val new_chatEdit : EditText = EditText(context)

        chatroom_add_btn.setOnClickListener {
            //팝업창을 띄움
            var builder = AlertDialog.Builder(context)
            builder.setTitle("채팅방 생성")
            builder.setView(new_chatEdit)
            builder.setPositiveButton("완료", DialogInterface.OnClickListener { dialog, which ->
                val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
                var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()
                var new_chatName = new_chatEdit.text.toString()

                var params:HashMap<String, Any> = HashMap<String, Any>()
                params.put("name", new_chatName)

                ServerUtil.retrofitService.requestCreateChat(accessToken,studyId,params)
                    .enqueue(object : Callback<ChatRoom> {
                        override fun onResponse(call: Call<ChatRoom>, response: Response<ChatRoom>) {
                            if (response.isSuccessful) {
                                val new_chatInfo = response.body()

                                Log.d(TAG, "채팅방 생성 성공")

                                //새로운 채팅방으로 감
                                val intent = Intent(getActivity(), StudyChatActivity::class.java)
                                intent.putExtra("studyChatId",new_chatInfo!!.id)
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            }
                        }

                        override fun onFailure(call: Call<ChatRoom>, t: Throwable) {
                            Log.d(TAG, "채팅방 생성 실패")
                        }
                    })
            })
            builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                Log.d(TAG, "취소")
            })
            builder.show()
        }

    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.chatitem_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.menu_modify -> {
            }
            R.id.menu_delete -> {
            }
        }
        return super.onContextItemSelected(item)
    }
}