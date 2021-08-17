package com.example.graduatedproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.example.graduatedproject.Adapter.StudyMemberAdapter
import com.example.graduatedproject.R
import kotlinx.android.synthetic.main.activity_study_member.*

class StudyMemberActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_member)

        setTopNavigation()
    }
    private fun setTopNavigation(){

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.top_navi_member_tab, null, false)

        val studyRoomId = intent.getIntExtra("studyRoomId",0)

        member_frag_pager.adapter = StudyMemberAdapter(supportFragmentManager, 2,studyRoomId)
        member_top_menu.setupWithViewPager(member_frag_pager)

        member_top_menu.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_top_navi_member_tab) as RelativeLayout
        member_top_menu.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_top_navi_member_add_tab) as RelativeLayout

    }
}