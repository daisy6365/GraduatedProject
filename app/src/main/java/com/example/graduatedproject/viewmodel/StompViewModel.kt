package com.example.graduatedproject.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.graduatedproject.model.Message
import com.example.graduatedproject.model.SendMessage
import com.google.gson.Gson
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompCommand
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.*


class StompViewModel : ViewModel() {
    //채팅 내용을 기록하는 뷰모델
    //채팅 리스트를 기록하는 뷰모델 필요 -> 스터디룸id에 따른 채팅방id, 채팅방 이름
    private val TAG = StompViewModel::class.java.simpleName

    private val SOCKET_URL = "ws://54.180.75.139:8000/chat-service/ws-stomp/websocket" // http = ws로 시작하며 https = wss로 시작
    private val MSSAGE_DESTINATION = "/sub/chat/room" // 소켓 주소
    private val MSSAGE_DESTINATION1 = "/pub/chat/message" // 소켓 주소

    private lateinit var mStompClient: StompClient
    private val gson = Gson()
    private var headerList = ArrayList<StompHeader>()
    private var sendList = ArrayList<StompHeader>()

    private val _message = MutableLiveData<Message>()
    val message: LiveData<Message>
        get() = _message

    @SuppressLint("CheckResult")
    fun connectStomp(studyChatId: Int, accesstoken : String) {
        // add Header
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL)
        headerList = ArrayList()
        headerList.add(StompHeader("token", accesstoken))
        mStompClient.connect(headerList)
        mStompClient.topic(MSSAGE_DESTINATION + "/" + studyChatId,headerList)
            .subscribe { stompMessage ->
                Log.d(TAG, "receive messageData :" + stompMessage.payload)
                val messageVO = gson.fromJson(stompMessage.payload, Message::class.java)
                _message.postValue(messageVO) }
        mStompClient.lifecycle().subscribe { lifecycleEvent: LifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> Log.i(
                    TAG,
                    "Stomp connection opened")
                LifecycleEvent.Type.ERROR -> { Log.i(
                    TAG, "Error",
                    lifecycleEvent.exception)
                    connectStomp(studyChatId,accesstoken) }
                LifecycleEvent.Type.CLOSED -> Log.i(
                    TAG,
                    "Stomp connection closed")
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.i(
                    TAG,
                    "FAILED_SERVER_HEARTBEAT")
            }
        }
    }

    fun sendMessage(message: String, studyChatId:Int, accesstoken : String) {
        val messageVO = SendMessage(message, studyChatId)
        val messageJson: String = gson.toJson(messageVO)

        sendList = ArrayList()
        sendList.add(StompHeader("token", accesstoken))
        sendList.add(StompHeader(StompHeader.DESTINATION, MSSAGE_DESTINATION1))
        //+ "/" + studyChatId
        mStompClient.send(StompMessage(StompCommand.SEND, sendList, messageJson)).subscribe()
        //mStompClient.send(MSSAGE_DESTINATION1 + "/" + studyChatId, Stomp).subscribe()
        Log.d(TAG, "send messageData : $messageJson")
    }

    fun disconnectStomp(){
        mStompClient.disconnect()
    }
}