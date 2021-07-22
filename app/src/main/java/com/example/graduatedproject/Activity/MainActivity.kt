package com.example.graduatedproject.Activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.graduatedproject.Adapter.AfterLoginAdapter
import com.example.graduatedproject.Adapter.BeforeLoginAdpater
import com.example.graduatedproject.R
import com.example.graduatedproject.R.id.btn_bottom_navi_mypage_tab
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.util.helper.Utility
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pref: SharedPreferences = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
//        val editor = pref.edit();
//        editor.remove("access_token")
//        editor.remove("refresh_token")
//        editor.commit()
        var token = pref.getString("access_token", "").toString()
        var fcmToken : String

        FirebaseApp.initializeApp(this);
//        System.out.println("token : "+ FirebaseInstanceId.getInstance().getToken()); // 토큰을 확인할 수 있는 코드
//        var fcmToken = FirebaseInstanceId.getInstance().getToken()!!

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                fcmToken = task.result
                Log.d("FCM토큰",fcmToken)

            })


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