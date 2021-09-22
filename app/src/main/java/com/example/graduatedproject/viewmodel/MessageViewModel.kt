package com.example.graduatedproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graduatedproject.model.ChatList

class MessageViewModel : ViewModel() {
    private val TAG = MessageViewModel::class.java.simpleName

    private val _messageInfo: MutableLiveData<ChatList> = MutableLiveData()

    val messageInfo: LiveData<ChatList>
        get() = _messageInfo

    //뷰모델일 생성되었을때의 초기값
    init {
        _messageInfo.value = null
    }

    fun setData(messageInfo: ChatList) {
        _messageInfo.value = messageInfo
    }
}