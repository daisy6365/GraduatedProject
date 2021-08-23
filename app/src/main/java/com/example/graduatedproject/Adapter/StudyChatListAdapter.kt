package com.example.graduatedproject.Adapter

import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Activity.StudyChatActivity
import com.example.graduatedproject.Model.ChatRoom
import com.example.graduatedproject.R


class StudyChatListAdapter (
    private var context : Context,
    var chatRoomInfo: ArrayList<ChatRoom>?
) : RecyclerView.Adapter<StudyChatListAdapter.StudyChatListViewHolder>() {

    class StudyChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chatroom_name: TextView
        var chatroom_menu: ImageView

        init {
            chatroom_name = itemView.findViewById(R.id.chatroom_name)
            chatroom_menu = itemView.findViewById(R.id.chatroom_menu)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyChatListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_chatroom, parent, false)

        return StudyChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudyChatListViewHolder, position: Int) {
        holder.chatroom_menu.setOnClickListener {
            holder.itemView.showContextMenu();
        }
    }

    private fun moveDetail(studyId: Int) {
        val intent : Intent = Intent(context, StudyChatActivity::class.java)
        intent.putExtra("studyRoomId",studyId)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }


    override fun getItemCount(): Int {
        if(chatRoomInfo == null){ //Replace "messages" with whatever array you are passing
            return 0
        }
        return chatRoomInfo!!.size
    }


}