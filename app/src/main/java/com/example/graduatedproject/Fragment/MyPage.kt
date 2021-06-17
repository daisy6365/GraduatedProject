package com.example.graduatedproject.Fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.graduatedproject.Activity.MainActivity
import com.example.graduatedproject.Model.LogdeleteDTO
import com.example.graduatedproject.Model.LogoutDTO
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.gson.JsonObject
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyPage.newInstance] factory method to
 * create an instance of this fragment.
 * var pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
var token:String = pref.getString("userToken", "").toString()
 */
class MyPage : Fragment() {
    val PREFERENCE = "SharedPreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("Life_cycle", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)


        //로그아웃
        val my_page_logout_btn : Button = view.findViewById(R.id.my_page_logout_btn)
        val myPage = this
        my_page_logout_btn.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(getActivity(),"로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(getActivity(), "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }

                //startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
            }
            val paramObject = JsonObject()

            ServerUtil.retrofitService.requestLogout(paramObject)
                .enqueue(object : Callback<LogoutDTO> {
                    override fun onResponse(call: Call<LogoutDTO>, response: Response<LogoutDTO>) {
                        if (response.isSuccessful) {
                            //accessToken을 가져옴
                            val pref = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
                            var accessToken :String = pref.getString("access_token", "").toString()

                            //accesstoken을 서버에 보냄 -> 로그아웃 요청
                            paramObject.addProperty("access_token", accessToken)

                            Log.d(TAG, "로그아웃 성공")
                            Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                            //메인화면으로 전환
                            val intent = Intent(getActivity(), MainActivity::class.java)
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        }
                    }

                    override fun onFailure(call: Call<LogoutDTO>, t: Throwable) {
                        Log.d(TAG, "로그아웃 실패")
                        Toast.makeText(getActivity(), "로그아웃 실패", Toast.LENGTH_LONG).show()
                    }
                })

        }


        //회원탈퇴
        val my_page_delete_btn : Button = view.findViewById(R.id.my_page_delete_btn)
        my_page_delete_btn.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(getActivity(), "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(getActivity(), "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    //val intent = Intent(this, LoginActivity::class.java)
                    //startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
            val paramObject = JsonObject()

            ServerUtil.retrofitService.requestLogdelete(paramObject)
                .enqueue(object : Callback<LogdeleteDTO> {
                    override fun onResponse(call: Call<LogdeleteDTO>, response: Response<LogdeleteDTO>) {
                        if (response.isSuccessful) {
                            //accessToken을 가져옴
                            val pref = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
                            var accessToken :String = pref.getString("access_token", "").toString()

                            //accesstoken을 서버에 보냄 -> 회원탈퇴 요청
                            paramObject.addProperty("access_token", accessToken)

                            Log.d(TAG, "회원탈퇴 성공")
                            Toast.makeText(getActivity(), "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                            //메인화면으로 전환
                            val intent = Intent(getActivity(), MainActivity::class.java)
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        }
                    }

                    override fun onFailure(call: Call<LogdeleteDTO>, t: Throwable) {
                        Log.d(TAG, "회원탈퇴 실패")
                        Toast.makeText(getActivity(), "회원탈퇴 실패", Toast.LENGTH_LONG).show()
                    }
                })


        }
    }

}