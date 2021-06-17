package com.example.graduatedproject.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.graduatedproject.Fragment.*

class BeforeLoginAdpater (fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int) : Fragment {


        when(position){
            0 -> return Home()
            1 -> return Login()
            2 -> return Login()
            3 -> return MyPageBeforeLogin()
            else -> return Home()
        }
    }

    override fun getCount(): Int = fragmentCount // 자바에서는 { return fragmentCount }

}