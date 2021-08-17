package com.example.graduatedproject.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.graduatedproject.Fragment.StudyAddMember
import com.example.graduatedproject.Fragment.StudyMember

class StudyMemberAdapter(fm : FragmentManager,
                         val fragmentCount : Int,
                         val studyRoomId : Int) : FragmentStatePagerAdapter(fm)  {
    override fun getItem(position: Int) : Fragment {
        when (position) {
            0 -> return StudyMember(studyRoomId)
            1 -> return StudyAddMember(studyRoomId)
            else -> return StudyMember(studyRoomId)
        }
    }

    override fun getCount(): Int = fragmentCount
}