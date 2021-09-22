package com.example.graduatedproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graduatedproject.model.Group
import com.example.graduatedproject.model.GroupList

class GroupListViewModel : ViewModel() {
    private val TAG = GroupListViewModel::class.java.simpleName

    private val _groupListInfo: MutableLiveData<GroupList> = MutableLiveData()

    val groupListInfo: LiveData<GroupList>
        get() = _groupListInfo

    //뷰모델일 생성되었을때의 초기값
    init {
        _groupListInfo.value = null
    }


    fun setData(groupListInfo: GroupList) {
        _groupListInfo.value = groupListInfo
    }

    fun addData(groupInfo : Group){
        _groupListInfo.value?.content!!.add(groupInfo)
    }

    fun modifyData(groupInfo: Group){
        for(i in 0 .. _groupListInfo.value!!.size-1){
            if(_groupListInfo.value!!.content[i].id == groupInfo.id){
                _groupListInfo.value!!.content[i].gatheringTime = groupInfo.gatheringTime
                _groupListInfo.value!!.content[i].shape = groupInfo.shape
                _groupListInfo.value!!.content[i].place!!.name = groupInfo.place!!.name
            }
        }
    }
}