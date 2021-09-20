package com.example.graduatedproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graduatedproject.Model.Group
import com.example.graduatedproject.Model.Profile

class MemberListViewModel : ViewModel() {
    private val TAG = MemberListViewModel::class.java.simpleName

    private val _memberInfo: MutableLiveData<ArrayList<Profile>> = MutableLiveData()

    val memberInfo: LiveData<ArrayList<Profile>>
        get() = _memberInfo

    //뷰모델일 생성되었을때의 초기값
    init {
        _memberInfo.value = null
    }


    fun setData(groupInfo: ArrayList<Profile>) {
        _memberInfo.value = groupInfo
    }
}