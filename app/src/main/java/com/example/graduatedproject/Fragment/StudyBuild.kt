package com.example.graduatedproject.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.graduatedproject.Activity.StudyMemberActivity
import com.example.graduatedproject.Activity.StudyModifyActivity
import com.example.graduatedproject.R

class StudyBuild : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_study_build, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val member_build : LinearLayout = view.findViewById(R.id.member_build)
        val study_modify : LinearLayout = view.findViewById(R.id.study_modify)

        member_build.setOnClickListener {
            activity?.let {
                val intent = Intent(context, StudyMemberActivity::class.java)
                startActivity(intent)
            }
        }

        study_modify.setOnClickListener {
            activity?.let {
                val intent = Intent(context, StudyModifyActivity::class.java)
                startActivity(intent)
            }
        }
    }
}