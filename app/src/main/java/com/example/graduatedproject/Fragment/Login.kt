package com.example.graduatedproject.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.graduatedproject.Activity.LoginActivity
import com.example.graduatedproject.R
class Login : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("Life_cycle","onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        val login_text : TextView = view.findViewById(R.id.login_text)
        login_text.setOnClickListener {
            activity?.let{
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }

    }
}