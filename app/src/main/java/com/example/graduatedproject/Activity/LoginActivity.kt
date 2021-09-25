package com.example.graduatedproject.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //val keyHash = Utility.getKeyHash(this)
        //Log.d("Hash", keyHash)

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show() //기타 오류
                    }
                }
            } //각종 로그인 오류 토스트 메시지 호출

            else if (token != null) {
                var kakaoToken : String = ""
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show() //로그인 성공시 activity2로 이동
                kakaoToken = token.accessToken
                Log.d("kakaoToken",kakaoToken)
                val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
                val editor = sp.edit()
                var fcmToken = sp.getString("fcm_token", "").toString()

                ServerUtil.retrofitService.requestLogin(kakaoToken,fcmToken)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                //서버에서 받은 AccessToken과 RefreshToken을 받아옴

                                var accessToken = response.headers().get("accessToken").toString()
                                Log.d(TAG,accessToken)
                                //headers()!!.accessToken
                                var refreshToken = response.headers()!!.get("refreshToken").toString()
                                Log.d(TAG,refreshToken)

                                //토큰들을 SharedPreference에 저장
                                editor.putString("access_token", accessToken)
                                editor.putString("refresh_token", refreshToken)
                                editor.commit()

                                Log.d(TAG, "로그인 성공")
                                Toast.makeText(this@LoginActivity, "로그인에 성공하였습니다.", Toast.LENGTH_LONG).show()
                                //메인화면으로 전환

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.d(TAG, "로그인 실패")
                            Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_LONG).show()
                        }
                    })

            }
        }

        kakao_login_button.setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            }else{
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }
}