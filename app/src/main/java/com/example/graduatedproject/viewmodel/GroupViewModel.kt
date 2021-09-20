package com.example.graduatedproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graduatedproject.Model.Group

class GroupViewModel : ViewModel() {
    private val TAG = GroupViewModel::class.java.simpleName

    private val _groupInfo: MutableLiveData<Group> = MutableLiveData()

    val groupInfo: LiveData<Group>
        get() = _groupInfo

    //뷰모델일 생성되었을때의 초기값
    init {
        _groupInfo.value = null
    }


    fun setData(groupInfo: Group) {
        _groupInfo.value = groupInfo
    }
}