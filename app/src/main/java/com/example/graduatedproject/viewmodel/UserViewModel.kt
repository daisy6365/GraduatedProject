package com.example.graduatedproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graduatedproject.Model.Profile

//데이터의 변경을 알려줌

class UserViewModel : ViewModel() {
    //MutableLiveData - 수정가능함
    //LiveData - 수정 불가능 -> 읽기전용
    private val _users: MutableLiveData<Profile> = MutableLiveData()

    val users: LiveData<Profile>
        get() = _users

    //뷰모델일 생성되었을때의 초기값
    init {
        _users.value = null
    }


    fun setData(profileInfo: Profile) {
        _users.value = profileInfo
    }
}