package com.example.graduatedproject.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.graduatedproject.Fragment.StudyAddMember
import com.example.graduatedproject.Fragment.StudyMember

class StudyMemberAdapter(fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm)  {
    override fun getItem(position: Int) : Fragment {
        when (position) {
            0 -> return StudyMember()
            1 -> return StudyAddMember()
            else -> return StudyMember()
        }
    }

    override fun getCount(): Int = fragmentCount
}