package com.example.graduatedproject.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.graduatedproject.Activity.LiketopicActivity
import com.example.graduatedproject.Activity.MainActivity
import com.example.graduatedproject.Activity.MapActivity
import com.example.graduatedproject.Model.Image
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.example.graduatedproject.viewmodel.UserViewModel
import com.example.graduatedproject.databinding.FragmentMyPageBinding
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_my_page.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPage : Fragment() {
    val TAG = MyPage::class.java.simpleName

    private lateinit var binding: FragmentMyPageBinding
    private lateinit var user : UserViewModel
    var nickname : String? = null
    var imageUrl : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        //accessToken을 가져옴
        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        //프로필 정보 요청
        var profile = Profile()
        ServerUtil.retrofitService.requestProfile(accessToken)
            .enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        profile = response.body()!!
                        user.setData(profile)


                        Log.d(TAG, "프로필 받기 성공")
                    }
                }
                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.d(TAG, "프로필 받기 실패")
                }
            })
        user = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        binding.viewModel = user

        user.users.observe(viewLifecycleOwner, Observer {
            if(it != null){
                binding.myPageName.text = it.nickName
                if(it.image == null){
                    Glide.with(requireView().context)
                        .load(R.drawable.profile_init)
                        .into(my_page_profile)
                }
                else{
                    bindImage(binding.myPageProfile,it.image.profileImage)
                }
            }
        })


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("Life_cycle", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)


        var profile_modify_btn : ImageView = view.findViewById(R.id.profile_modify_btn)
        profile_modify_btn.setOnClickListener {
            //수정 버튼 누르면 새로운 프래그먼트 띄우기
            //EditProfile.kt 띄우기
            //Fragment에서 FragmentDialog를 호출하도록 코드 추가

            val dialog = EditProfile().getInstance()
            dialog.show(requireActivity().getSupportFragmentManager(),"tag")
        }

        val my_page_apply_btn : Button = view.findViewById(R.id.my_page_apply_btn)
        my_page_apply_btn.setOnClickListener {
            val dialog = StudyApplyStatus().getInstance()
            dialog.show(requireActivity().getSupportFragmentManager(), "StudyApplyStatus")
        }



        //관심주제
        val my_page_likes_btn: Button = view.findViewById(R.id.my_page_likes_btn)
        my_page_likes_btn.setOnClickListener {
            activity?.let {
                val intent = Intent(context, LiketopicActivity::class.java)
                startActivity(intent)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        //동네정보
        val my_page_place_btn: Button = view.findViewById(R.id.my_page_place_btn)
        my_page_place_btn.setOnClickListener {
            activity?.let {
                val intent = Intent(context, MapActivity::class.java)
                startActivity(intent)
            }

        }


        //로그아웃
        val my_page_logout_btn: Button = view.findViewById(R.id.my_page_logout_btn)

        /**
         * 만약 카카오 토큰이 존재한다면 만료시키고 없으면 그냥 아무것도 하지마
         * 그리고 우리 서버에다가 로그아웃 요청을 하자
         */
        my_page_logout_btn.setOnClickListener {
            //accessToken을 가져옴
            val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Toast.makeText(getActivity(), "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            //accesstoken을 서버에 보냄 -> 로그아웃 요청
            ServerUtil.retrofitService.requestLogout(accessToken)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            val editor = pref.edit();
                            editor.remove("access_token")
                            editor.remove("refresh_token")
                            editor.commit()

                            Log.d(TAG, "로그아웃 성공")
                            Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                            //메인화면으로 전환
                            val intent = Intent(getActivity(), MainActivity::class.java)
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        }

                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d(TAG, "로그아웃 실패")
                    }
                })
        }


        //회원탈퇴
        val my_page_delete_btn: Button = view.findViewById(R.id.my_page_delete_btn)
        my_page_delete_btn.setOnClickListener {
            //accessToken을 가져옴
            val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(getActivity(), "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()

                } else {
                    ServerUtil.retrofitService.requestLogdelete(accessToken)
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {

                                    Log.d(TAG, "회원탈퇴 성공")
                                    Toast.makeText(getActivity(), "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT)
                                        .show();

                                    val editor = pref.edit();
                                    editor.remove("access_token")
                                    editor.remove("refresh_token")
                                    editor.commit()

                                    //메인화면으로 전환
                                    val intent = Intent(getActivity(), MainActivity::class.java)
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.d(TAG, "회원탈퇴 실패")
                            }
                        })
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("bind:imageUrl")
        fun bindImage(view:ImageView,url: String?) {
            Glide.with(view.context)
                .load(url)
                .into(view)
        }
    }
}