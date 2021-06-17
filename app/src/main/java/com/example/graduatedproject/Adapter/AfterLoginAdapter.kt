package com.example.graduatedproject.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.graduatedproject.Fragment.Home
import com.example.graduatedproject.Fragment.MyPage
import com.example.graduatedproject.Fragment.MyStudy
import com.example.graduatedproject.Fragment.fcmNotice

class AfterLoginAdapter (fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int) : Fragment {


        when(position){
            0 -> return Home()
            1 -> return MyStudy()
            2 -> return fcmNotice()
            3 -> return MyPage()
            else -> return Home()
        }
    }

    override fun getCount(): Int = fragmentCount // 자바에서는 { return fragmentCount }

}