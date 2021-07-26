package com.example.graduatedproject.Activity

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout

import androidx.appcompat.app.AppCompatActivity
import com.example.graduatedproject.Adapter.StudyRoomAdapter
import com.example.graduatedproject.R
import kotlinx.android.synthetic.main.activity_study_room.*


class StudyRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_room)

        setTopNavigation()
    }
    private fun setTopNavigation(){

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.top_navigation_tab, null, false)


        study_frag_pager.adapter = StudyRoomAdapter(supportFragmentManager, 4)
        study_top_menu.setupWithViewPager(study_frag_pager)

        study_top_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.top_navi_home_tab) as RelativeLayout
        study_top_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.top_navi_chat_tab) as RelativeLayout
        study_top_menu.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.top_navi_group_tab) as RelativeLayout
        study_top_menu.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(R.id.top_navi_build_tab) as RelativeLayout

    }
}
