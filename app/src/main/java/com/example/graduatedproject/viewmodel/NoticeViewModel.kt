package com.example.graduatedproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graduatedproject.model.Notice
import com.example.graduatedproject.model.NotificationList

class NoticeViewModel : ViewModel() {
    private val _noticeListInfo: MutableLiveData<NotificationList> = MutableLiveData()

    val noticeListInfo: LiveData<NotificationList>
        get() = _noticeListInfo

    //뷰모델일 생성되었을때의 초기값
    init {
        _noticeListInfo.value = null
    }

    fun setData(chatListInfo: NotificationList) {
        _noticeListInfo.value = chatListInfo
    }

    fun addData(chatInfo : Notice){
        _noticeListInfo.value?.content!!.add(chatInfo)
    }
}