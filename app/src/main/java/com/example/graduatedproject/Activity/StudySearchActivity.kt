package com.example.graduatedproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.graduatedproject.R

class StudySearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_search)

        if (intent.hasExtra("searchKeyword")) {
            val offline = intent.getBooleanExtra("offline",false)
            val online = intent.getBooleanExtra("online",false)
            val searchKeyword = intent.getStringExtra("searchKeyword")
            val categoryId = intent.getIntExtra("categoryId",0)

            Log.d("인텐트 가져온값", offline.toString())
            Log.d("인텐트 가져온값", online.toString())
            Log.d("인텐트 가져온값", searchKeyword!!)
            Log.d("인텐트 가져온값", categoryId.toString())

        }
        else{
        }
    }
}