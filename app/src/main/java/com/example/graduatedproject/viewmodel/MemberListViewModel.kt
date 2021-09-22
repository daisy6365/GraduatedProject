package com.example.graduatedproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graduatedproject.model.Profile

class MemberListViewModel : ViewModel() {
    private val _memberListInfo: MutableLiveData<ArrayList<Profile>> = MutableLiveData()

    val memberListInfo: LiveData<ArrayList<Profile>>
        get() = _memberListInfo

    //뷰모델일 생성되었을때의 초기값
    init {
        _memberListInfo.value = null
    }


    fun setData(memberListInfo: ArrayList<Profile>) {
        _memberListInfo.value = memberListInfo
    }

    fun addData(memberInfo : Profile){
        _memberListInfo.value!!.add(memberInfo)
    }
}