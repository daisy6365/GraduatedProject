package com.example.graduatedproject.Activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.example.graduatedproject.Adapter.AfterLoginAdapter
import com.example.graduatedproject.Adapter.BeforeLoginAdpater
import com.example.graduatedproject.R
import com.example.graduatedproject.R.id.btn_bottom_navi_mypage_tab
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var token : String
    val PREFERENCE = "SharedPreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureBottomNavigation()
    }

    private fun configureBottomNavigation(){

        val pref : SharedPreferences = getSharedPreferences("pref",Context.MODE_PRIVATE)
        token = pref.getString("userToken", "").toString()

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)


        if(token!!.isEmpty()){
            main_frag_pager.adapter = BeforeLoginAdpater(supportFragmentManager, 4)
            main_bottom_menu.setupWithViewPager(main_frag_pager)

            main_bottom_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
            main_bottom_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_study_tab) as RelativeLayout
            main_bottom_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_notice_tab) as RelativeLayout
            main_bottom_menu.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(btn_bottom_navi_mypage_tab) as RelativeLayout

        }
        else if(token!!.isNotEmpty()){
            main_frag_pager.adapter = AfterLoginAdapter(supportFragmentManager, 4)
            main_bottom_menu.setupWithViewPager(main_frag_pager)

            main_bottom_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
            main_bottom_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_study_tab) as RelativeLayout
            main_bottom_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_notice_tab) as RelativeLayout
            main_bottom_menu.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(btn_bottom_navi_mypage_tab) as RelativeLayout

        }



    }

}