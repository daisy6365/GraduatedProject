package com.example.graduatedproject.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.graduatedproject.Adapter.AfterLoginAdapter
import com.example.graduatedproject.Adapter.BeforeLoginAdpater
import com.example.graduatedproject.R
import com.example.graduatedproject.R.id.btn_bottom_navi_mypage_tab
import com.example.graduatedproject.Util.ServerUtil
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var accessToken : String
    lateinit var kakaoToken : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //accessToken & kakaoToken 받기
        val pref : SharedPreferences = getSharedPreferences("login_sp",Context.MODE_PRIVATE)
        accessToken = pref.getString("access_token", "").toString()
        kakaoToken = pref.getString("kakao_token", "").toString()


        configureBottomNavigation(accessToken)
    }

    private fun configureBottomNavigation(accessToken : String){
        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        if(accessToken.isEmpty()){
            main_frag_pager.adapter = BeforeLoginAdpater(supportFragmentManager, 4)
            main_bottom_menu.setupWithViewPager(main_frag_pager)

            main_bottom_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
            main_bottom_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_study_tab) as RelativeLayout
            main_bottom_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_notice_tab) as RelativeLayout
            main_bottom_menu.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(btn_bottom_navi_mypage_tab) as RelativeLayout
        }

        else if(accessToken.isNotEmpty()){
            main_frag_pager.adapter = AfterLoginAdapter(supportFragmentManager, 4)
            main_bottom_menu.setupWithViewPager(main_frag_pager)

            main_bottom_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
            main_bottom_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_study_tab) as RelativeLayout
            main_bottom_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_notice_tab) as RelativeLayout
            main_bottom_menu.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(btn_bottom_navi_mypage_tab) as RelativeLayout

        }
    }
}