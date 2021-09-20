package com.example.graduatedproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graduatedproject.Model.ChatRoom
import com.example.graduatedproject.Model.Group

class GroupListViewModel : ViewModel() {
    private val TAG = GroupListViewModel::class.java.simpleName

    private val _groupListInfo: MutableLiveData<ArrayList<Group>> = MutableLiveData()

    val groupListInfo: LiveData<ArrayList<Group>>
        get() = _groupListInfo

    //뷰모델일 생성되었을때의 초기값
    init {
        _groupListInfo.value = null
    }


    fun setData(groupListInfo: ArrayList<Group>) {
        _groupListInfo.value = groupListInfo
    }
}