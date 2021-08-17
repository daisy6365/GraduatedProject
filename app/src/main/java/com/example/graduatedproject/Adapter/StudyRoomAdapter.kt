package com.example.graduatedproject.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.graduatedproject.Fragment.StudyChat
import com.example.graduatedproject.Fragment.StudyGroupList
import com.example.graduatedproject.Fragment.StudyHome
import com.example.graduatedproject.Fragment.StudyBuild

class StudyRoomAdapter(fm : FragmentManager,
                       val fragmentCount : Int,
                       val studyRoomId : Int) : FragmentStatePagerAdapter(fm)  {
    override fun getItem(position: Int) : Fragment {
        when (position) {
            0 -> return StudyHome(studyRoomId)
            1 -> return StudyChat(studyRoomId)
            2 -> return StudyGroupList(studyRoomId)
            3 -> return StudyBuild(studyRoomId)
            else -> return StudyHome(studyRoomId)
        }
    }

        override fun getCount(): Int = fragmentCount
}