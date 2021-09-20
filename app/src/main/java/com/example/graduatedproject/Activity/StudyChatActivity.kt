package com.example.graduatedproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.graduatedproject.Fragment.MyPage
import com.example.graduatedproject.R
import com.example.graduatedproject.databinding.FragmentMyPageBinding
import com.example.graduatedproject.viewmodel.ChatViewModel
import com.example.graduatedproject.viewmodel.UserViewModel
import java.lang.Thread.sleep

class StudyChatActivity : AppCompatActivity() {
    val TAG = StudyChatActivity::class.java.simpleName

    private val chatViewModel: ChatViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var pref = getSharedPreferences("login_sp", MODE_PRIVATE)
        var accessToken: String = pref.getString("access_token", "").toString()
        val studyChatId = intent.getIntExtra("studyChatId",0)

        chatViewModel.connectStomp(studyChatId, accessToken)

        sleep(1000)

        chatViewModel.sendMessage("다빈이 최고 예쁨!!",1,accessToken)
    }
    override fun onBackPressed() {
        chatViewModel.disconnectStomp()
        super.onBackPressed()
    }
}