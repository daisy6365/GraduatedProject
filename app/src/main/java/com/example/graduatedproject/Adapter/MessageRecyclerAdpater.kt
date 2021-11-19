package com.example.graduatedproject.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Activity.StudyChatActivity
import com.example.graduatedproject.model.Message
import com.example.graduatedproject.databinding.ItemChatMeBinding
import com.example.graduatedproject.databinding.ItemChatYouBinding

class MessageRecyclerAdpater(val userId : Int?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messageInfo =  ArrayList<Message>()

    class MyViewHolder(val binding : ItemChatMeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message){
            val str = message.createdAt
            val idT: Int = str!!.indexOf("T")
            val time = str.substring(idT+1)

            val timeList = time.split(":")
            val time_group = timeList[0] + ":" + timeList[1]
            binding.myMessagge.text = message.message.toString()
            binding.myTime.text = time_group
        }
    }
    class YouViewHolder(val binding : ItemChatYouBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message){
            val str = message.createdAt
            val idT: Int = str!!.indexOf("T")
            val time = str.substring(idT+1)

            val timeList = time.split(":")
            val time_group = timeList[0] + ":" + timeList[1]

            binding.otherName.text = message.sender.toString()
            binding.otherMessage.text = message.message.toString()
            binding.otherTime.text = time_group

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        when (viewType) {
            StudyChatActivity.MY -> {
                val binding =
                    ItemChatMeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyViewHolder(binding)
            }
            StudyChatActivity.OTHER -> {
                val binding =
                    ItemChatYouBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return YouViewHolder(binding)
            }
            else -> {
                val binding =
                    ItemChatMeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageInfo[position]
        if (holder is MyViewHolder) holder.bind(message)
        else (holder as YouViewHolder).bind(message)

    }

    fun setList(notice: MutableList<Message>) {
        messageInfo.addAll(0,notice)
        notifyDataSetChanged()
    }

    fun add(message: Message) {
        messageInfo.add(message)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (messageInfo[position].userId!!) {
            userId -> StudyChatActivity.MY
            else -> StudyChatActivity.OTHER
        }
            //.type!! // 메세지 객체 타입 비교 후 홀더 구분
    }

    override fun getItemCount(): Int {
        if(messageInfo == null){ //Replace "messages" with whatever array you are passing
            return 0
        }
        return messageInfo!!.size
    }
}