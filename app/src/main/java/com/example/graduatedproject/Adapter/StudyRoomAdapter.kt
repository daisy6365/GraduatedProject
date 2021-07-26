package com.example.graduatedproject.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.graduatedproject.Fragment.StudyChat
import com.example.graduatedproject.Fragment.StudyGroupList
import com.example.graduatedproject.Fragment.StudyHome
import com.example.graduatedproject.Fragment.StudyModify

class StudyRoomAdapter(fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm)  {
    override fun getItem(position: Int) : Fragment {
        when (position) {
            0 -> return StudyHome()
            1 -> return StudyChat()
            2 -> return StudyGroupList()
            3 -> return StudyModify()
            else -> return StudyHome()
        }
    }

        override fun getCount(): Int = fragmentCount
}