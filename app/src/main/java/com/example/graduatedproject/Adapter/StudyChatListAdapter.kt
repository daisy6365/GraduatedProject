package com.example.graduatedproject.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        var home_study_name: TextView
        var search_study_cover: ImageView

        init {
            home_study_name = itemView.findViewById(R.id.home_study_name)
            search_study_cover = itemView.findViewById(R.id.search_study_cover)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyChatListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_chatroom, parent, false)

        return StudyChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudyChatListViewHolder, position: Int) {

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