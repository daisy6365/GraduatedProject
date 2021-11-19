package com.example.graduatedproject.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Activity.StudyChatActivity
import com.example.graduatedproject.ItemTouchHelperListener
import com.example.graduatedproject.model.ChatRoom
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.example.graduatedproject.viewmodel.ChatListViewModel
import org.jetbrains.anko.layoutInflater
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StudyChatListAdapter (
    private var context : Context,
    var chatRoomInfo: LiveData<ArrayList<ChatRoom>>?,
    var accessToken : String,
    private var viewmodel : ChatListViewModel,
) : RecyclerView.Adapter<StudyChatListAdapter.StudyChatListViewHolder>(), ItemTouchHelperListener {

    class StudyChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var chatroom_name: TextView
        var item_chatroom : RelativeLayout

        init {
            chatroom_name = itemView.findViewById(R.id.chatroom_name)
            item_chatroom = itemView.findViewById(R.id.item_chatroom)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyChatListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_chatroom, parent, false)

        return StudyChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudyChatListViewHolder, position: Int) {
        holder.chatroom_name.setText(chatRoomInfo!!.value!![position].name)
        holder.item_chatroom.setOnClickListener {
            for(i in 0 .. chatRoomInfo!!.value!!.size-1){
                if(holder.chatroom_name.text == chatRoomInfo!!.value!![i].name){
                    val chatId = chatRoomInfo!!.value!![i].id
                    val chatName = chatRoomInfo!!.value!![i].name
                    Log.d("userId", chatId.toString())

                    moveDetail(chatId,chatName)
                }
            }
        }
    }

    private fun moveDetail(chatId: Int,chatName : String) {
        val intent : Intent = Intent(context, StudyChatActivity::class.java)
        intent.putExtra("studyChatId",chatId)
        intent.putExtra("studyChatName",chatName)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }


    override fun getItemCount(): Int {
        if(chatRoomInfo == null){ //Replace "messages" with whatever array you are passing
            return 0
        }
        return chatRoomInfo!!.value!!.size
    }

    override fun onItemMove(from_position: Int, to_position: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onItemSwipe(position: Int) {
        chatRoomInfo!!.value!!.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onRightClick(position: Int, viewHolder: RecyclerView.ViewHolder) {
        Log.d("StudyChatListAdapter", chatRoomInfo!!.value!![position].id.toString())
        ServerUtil.retrofitService.requestDeleteChat(accessToken, chatRoomInfo!!.value!![position].id)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {

                        Log.d("StudyChatListAdapter", "채팅방  삭제 성공")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("StudyChatListAdapter", "채팅방 이름 삭제 실패")
                }
            })

        chatRoomInfo!!.value!!.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onLeftClick(position: Int, viewHolder: RecyclerView.ViewHolder) {
        //팝업창을 띄움
        val dialogView = context.layoutInflater.inflate(R.layout.dialog_chat, null)
        val dialogText = dialogView.findViewById<EditText>(R.id.chatname)

        var builder = AlertDialog.Builder(context)
        builder.setTitle("채팅방이름 수정")
        builder.setView(dialogView)
        builder.setPositiveButton("완료", DialogInterface.OnClickListener { dialog, which ->
            var new_chatName = dialogText.text.toString()

            var params:HashMap<String, Any> = HashMap<String, Any>()
            params.put("name", new_chatName)

            ServerUtil.retrofitService.requestModifyChat(accessToken, chatRoomInfo!!.value!![position].id,params)
                .enqueue(object : Callback<ChatRoom> {
                    override fun onResponse(call: Call<ChatRoom>, response: Response<ChatRoom>) {
                        if (response.isSuccessful) {
                            val new_chatInfo = response.body()
                            viewmodel.modifyData(position, new_chatInfo!!)
                            notifyDataSetChanged()
                            Log.d("StudyChatListAdapter", "채팅방 이름 수정 성공")
                        }
                    }

                    override fun onFailure(call: Call<ChatRoom>, t: Throwable) {
                        Log.d("StudyChatListAdapter", "채팅방 이름 수정 실패")
                    }
                })
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
            Log.d("StudyChatListAdapter", "취소")
        })
        builder.show()
    }


}