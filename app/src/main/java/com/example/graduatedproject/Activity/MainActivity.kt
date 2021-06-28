package com.example.graduatedproject.Activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.example.graduatedproject.Adapter.AfterLoginAdapter
import com.example.graduatedproject.Adapter.BeforeLoginAdpater
import com.example.graduatedproject.R
import com.example.graduatedproject.R.id.btn_bottom_navi_mypage_tab
import com.kakao.auth.api.AuthApi
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.util.helper.Utility
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pref: SharedPreferences = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var token = pref.getString("access_token", "").toString()

        /**
         * 1. 로그아웃을 할 때 sharedPreference 에 액세스 토큰을 지운다. 리프레시 토큰도
         * 2. 만약 로그인을 하고 그냥 껏다가 키면 액세스토큰이 존재하지만 카카오토큰이 존재하지 않는다.
         * 3. 여기서 카카오 토큰을 생성하면 되지 않을까??
         */

        configureBottomNavigation(token)

        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)
    }

    private fun configureBottomNavigation(token : String){

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)


        if(token.isEmpty()){
            main_frag_pager.adapter = BeforeLoginAdpater(supportFragmentManager, 4)
            main_bottom_menu.setupWithViewPager(main_frag_pager)

            main_bottom_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
            main_bottom_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_study_tab) as RelativeLayout
            main_bottom_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_notice_tab) as RelativeLayout
            main_bottom_menu.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(btn_bottom_navi_mypage_tab) as RelativeLayout

        }
        else if(token.isNotEmpty()){
            main_frag_pager.adapter = AfterLoginAdapter(supportFragmentManager, 4)
            main_bottom_menu.setupWithViewPager(main_frag_pager)

            main_bottom_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
            main_bottom_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_study_tab) as RelativeLayout
            main_bottom_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_notice_tab) as RelativeLayout
            main_bottom_menu.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(btn_bottom_navi_mypage_tab) as RelativeLayout

        }



    }

}