package com.example.graduatedproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.graduatedproject.R
import com.kakao.util.helper.Utility

class MapSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_search)

        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)
    }
}