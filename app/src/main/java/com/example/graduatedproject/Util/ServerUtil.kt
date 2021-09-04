package com.example.graduatedproject.Util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.graduatedproject.Activity.GlobalApplication
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServerUtil {
    private val TAG = this::class.java.simpleName
    var retrofitService: InfoService
    var kakaoService : InfoService

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val builder = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(AuthInterceptor(GlobalApplication.ApplicationContext()))
            .connectTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .build()

        //Retrofit : type-safe한 HTTP 클라이언트 라이브러리
        //retrofit은 Okhttp 클라이언트를 디폴트로 선언
        //다른 HTTP 모듈보다 빠름
        val retrofit = Retrofit
            .Builder()
            .baseUrl("http://211.37.147.101:8000")
            //Gson은 Json문서를 받아서 자동으로 java class 형태로 만들어주는 역할
            .addConverterFactory(GsonConverterFactory.create())
            .client(builder)
            .build()

        val kakaoretrofit = Retrofit
            .Builder()
            .baseUrl("https://dapi.kakao.com")
            //Gson은 Json문서를 받아서 자동으로 java class 형태로 만들어주는 역할
            .addConverterFactory(GsonConverterFactory.create())
            .client(builder)
            .build()

        retrofitService = retrofit.create(InfoService::class.java)
        kakaoService = kakaoretrofit.create(InfoService::class.java)
        Log.d(TAG, "서버 연결")

    }

}
class AuthInterceptor(private val context: Context) : Interceptor {
    private val TAG = AuthInterceptor::class.java.simpleName
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request();
        val response = chain.proceed(request);

        when (response.code()) {
            400 -> {
                //Show Bad Request Error Message
            }
            401 -> {
                //Show UnauthorizedError Message
                val pref: SharedPreferences = context.getSharedPreferences("login_sp", AppCompatActivity.MODE_PRIVATE)
                val refreshToken: String = "Bearer " + pref.getString("refresh_token", "").toString()
                Log.d(TAG,refreshToken)

                ServerUtil.retrofitService.requestToken(refreshToken)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                            if (response.isSuccessful) {
                                //서버에서 받은 AccessToken과 RefreshToken을 받아옴
                                val sp = context.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
                                val editor = sp.edit()

                                var accessToken = response.headers().get("accessToken").toString()
                                Log.d(TAG,accessToken)

                                //토큰들을 SharedPreference에 저장
                                editor.putString("access_token", accessToken)
                                editor.commit()

                                Log.d(TAG, "로그인 성공")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.d(TAG, "로그인 실패")
                        }
                    })
            }

            403 -> {
                //Show Forbidden Message
            }

            404 -> {
                //Show NotFound Message
            }

            // ... and so on

        }
        return response
    }


}