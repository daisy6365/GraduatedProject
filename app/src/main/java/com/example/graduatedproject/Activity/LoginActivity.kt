package com.example.graduatedproject.Activity

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.graduatedproject.Model.LoginDTO
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonObject
import org.jetbrains.anko.editText
import com.kakao.kakaotalk.StringSet.token as token1

class LoginActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.java.simpleName
    val PREFERENCE = "SharedPreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //val keyHash = Utility.getKeyHash(this)
        //Log.d("Hash", keyHash)

        lateinit var accessToken : String
        lateinit var refreshToken : String

        var kakaoToken : String = ""

        //한 번 로그인시 sdk에서 토큰 소유 -> 로그아웃,회원탈퇴를 할 시 토큰 삭제 -> 토큰을 확인해 유효한 토큰이 존재하는지 확인한 뒤
        // 토큰 존재시 로그인 상태이므로 SecondActivity로 넘겨주고, 토큰이 존재하지 않을시 MainActivity에 머무르게 하기.
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
            }
            else if (tokenInfo != null) {
                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
                //카카오토큰을 paramObject를 사용하여 서버로 보냄
                kakaoToken = tokenInfo.toString()

                //토큰을 sharedpreference에 저장
//                val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
//                val editor = sp.edit()
//                editor.putString("kakao_token", tokenInfo.toString())
//                editor.commit()
            }
        }//토큰 확인 코드

        ServerUtil.retrofitService.requestLogin(kakaoToken)
            .enqueue(object : Callback<LoginDTO> {
                override fun onResponse(call: Call<LoginDTO>, response: Response<LoginDTO>) {
                    if (response.isSuccessful) {
                        //서버에서 받은 AccessToken과 RefreshToken을 받아옴

                        accessToken = response.headers().get("accessToken").toString()
                        //headers()!!.accessToken
                        refreshToken = response.headers()!!.get("refreshToken").toString();

                        //토큰들을 SharedPreference에 저장
                        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
                        val editor = sp.edit()
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

                override fun onFailure(call: Call<LoginDTO>, t: Throwable) {
                    Log.d(TAG, "로그인 실패")
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_LONG).show()
                }
            })


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
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show() //로그인 성공시 activity2로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }//.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) > 로그아웃,회원탈퇴를 할 시 이전 화면으로 이동
        }



        kakao_login_button.setOnClickListener {
            if(LoginClient.instance.isKakaoTalkLoginAvailable(this)){
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
            }else{
                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }





    }
}