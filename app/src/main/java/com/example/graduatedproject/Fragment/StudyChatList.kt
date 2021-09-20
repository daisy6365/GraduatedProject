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
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Activity.StudyChatActivity
import com.example.graduatedproject.Adapter.StudyChatListAdapter
import com.example.graduatedproject.ItemTouchHelperCallback
import com.example.graduatedproject.Model.ChatRoom
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.example.graduatedproject.databinding.FragmentStudyChatBinding
import com.example.graduatedproject.viewmodel.ChatListViewModel
import kotlinx.android.synthetic.main.fragment_study_chat.*
import org.jetbrains.anko.ems
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StudyChat(studyRoomId: Int) : Fragment() {
    private lateinit var binding : FragmentStudyChatBinding
    private lateinit var viewmodel : ChatListViewModel
    private var recyclerAdapter: StudyChatListAdapter? = null
    private val TAG = StudyChat::class.java.simpleName
    var chatRoomInfo : ArrayList<ChatRoom>? = null
    var studyId = studyRoomId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudyChatBinding.inflate(inflater, container, false)

        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestChatRoom(accessToken,studyId)
            .enqueue(object : Callback<ArrayList<ChatRoom>> {
                override fun onResponse(call: Call<ArrayList<ChatRoom>>, response: Response<ArrayList<ChatRoom>>) {
                    if (response.isSuccessful) {
                        chatRoomInfo = response.body()!!
                        viewmodel.setData(chatRoomInfo!!)
                        Log.d(TAG, chatRoomInfo.toString())

                        val recyclerView: RecyclerView = view!!.findViewById(R.id.chat_recycler)
                        binding.chatRecycler.layoutManager = LinearLayoutManager(activity)
                        setSwipeFunction()
                        //registerForContextMenu(recyclerView)


                        Log.d(TAG, "채팅방 리스트 정보 받기 성공")
                    }
                }
                override fun onFailure(call: Call<ArrayList<ChatRoom>>, t: Throwable) {
                    Log.d(TAG, "채팅방 리스트 정보 받기 실패")
                    Toast.makeText(getActivity(), "채팅방 리스트 정보 받기 실패", Toast.LENGTH_LONG).show()
                }
            })

        viewmodel = ViewModelProvider(requireActivity()).get(ChatListViewModel::class.java)

        viewmodel.chatListInfo.observe(viewLifecycleOwner, Observer {
            if(it != null){
                recyclerAdapter = StudyChatListAdapter(requireContext(),viewmodel.chatListInfo,accessToken,viewmodel)
                binding.chatRecycler.adapter = recyclerAdapter
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //채팅방 생성
        val chatroom_add_btn : LinearLayout = view.findViewById(R.id.chatroom_add_btn)

        chatroom_add_btn.setOnClickListener {
            //팝업창을 띄움
            val dialogView = layoutInflater.inflate(R.layout.dialog_chat, null)
            val dialogText = dialogView.findViewById<EditText>(R.id.chatname)

            var builder = AlertDialog.Builder(context)
            builder.setTitle("채팅방 생성")
            builder.setView(dialogView)
            builder.setPositiveButton("완료", DialogInterface.OnClickListener { dialog, which ->
                val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
                var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()
                var new_chatName = dialogText.text.toString()

                var params:HashMap<String, Any> = HashMap<String, Any>()
                params.put("name", new_chatName)

                ServerUtil.retrofitService.requestCreateChat(accessToken,studyId,params)
                    .enqueue(object : Callback<ChatRoom> {
                        override fun onResponse(call: Call<ChatRoom>, response: Response<ChatRoom>) {
                            if (response.isSuccessful) {
                                val new_chatInfo = response.body()
                                viewmodel.addData(new_chatInfo!!)

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

    fun setSwipeFunction() {
        val helper = ItemTouchHelper(ItemTouchHelperCallback(recyclerAdapter!!, requireContext()))
        helper.attachToRecyclerView(chat_recycler)

        val simpleCallback : ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                chatRoomInfo!!.removeAt(viewHolder.layoutPosition)
                recyclerAdapter!!.notifyItemRemoved(viewHolder.layoutPosition)

            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(chat_recycler)
    }
}