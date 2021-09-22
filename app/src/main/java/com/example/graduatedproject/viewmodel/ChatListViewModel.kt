package com.example.graduatedproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graduatedproject.model.ChatRoom

class ChatListViewModel : ViewModel() {
    private val TAG = ChatListViewModel::class.java.simpleName

    private val _chatListInfo: MutableLiveData<ArrayList<ChatRoom>> = MutableLiveData()

    val chatListInfo: LiveData<ArrayList<ChatRoom>>
        get() = _chatListInfo

    //뷰모델일 생성되었을때의 초기값
    init {
        _chatListInfo.value = null
    }

    fun setData(chatListInfo: ArrayList<ChatRoom>) {
        _chatListInfo.value = chatListInfo
    }

    fun addData(chatInfo : ChatRoom){
        _chatListInfo.value?.add(chatInfo)
    }
    fun modifyData(position : Int, chatInfo: ChatRoom){
        _chatListInfo.value!![position].id = chatInfo.id
        _chatListInfo.value!![position].name = chatInfo.name
        _chatListInfo.value!![position].studyId = chatInfo.studyId
    }
}