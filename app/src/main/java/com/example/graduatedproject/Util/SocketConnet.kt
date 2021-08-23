package com.example.graduatedproject.Util

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.graduatedproject.Model.Message
import com.google.gson.Gson
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.text.SimpleDateFormat
import java.util.*

class SocketConnet {
    private val TAG = SocketConnet::class.java.simpleName

    private val SOCKET_URL = "wss://Your url/websocket" // http = ws로 시작하며 https = wss로 시작
    private val MSSAGE_DESTINATION = "/socket/message" // 소켓 주소

    private lateinit var mStompClient: StompClient
    private val gson = Gson()

    private val _message = MutableLiveData<Message>()
    val message: LiveData<Message>
        get() = _message

    @SuppressLint("CheckResult")
    fun connectStomp(room: String) {
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL)
        mStompClient.lifecycle().subscribe { lifecycleEvent: LifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> Log.i(
                    TAG,
                    "Stomp connection opened"
                )
                LifecycleEvent.Type.ERROR -> { Log.i(
                    TAG, "Error",
                    lifecycleEvent.exception
                )
                    connectStomp(room)
                }
                LifecycleEvent.Type.CLOSED -> Log.i(
                    TAG,
                    "Stomp connection closed"
                )
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.i(
                    TAG,
                    "FAILED_SERVER_HEARTBEAT"
                )
            }
        }
        mStompClient.topic(MSSAGE_DESTINATION + "/" + room)
            .subscribe { stompMessage ->
                Log.d(TAG, "receive messageData :" + stompMessage.payload)
                val messageVO = gson.fromJson(stompMessage.payload, Message::class.java)
                _message.postValue(messageVO)
            }
        mStompClient.connect()
    }

    fun sendMessage(sender: String, message: String, room: String) {   // 구독 하는 방과 같은 주소로 메세지 전송
        val createdAt = SimpleDateFormat("k:mm").format(Date(System.currentTimeMillis()))
        val messageDTO = Message(sender, message, createdAt)
        val messageJson: String = gson.toJson(message)
        mStompClient.send(MSSAGE_DESTINATION + "/" + room, messageJson).subscribe()
        Log.d(TAG, "send messageData : $messageJson")
    }

    fun disconnectStomp(){
        mStompClient.disconnect()
    }
}