package com.example.graduatedproject.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduatedproject.Adapter.MyStudyListAdapter
import com.example.graduatedproject.Model.Study
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import kotlinx.android.synthetic.main.fragment_my_study.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyStudy : Fragment() {
    private val TAG = StudyHome::class.java.simpleName
    var studyInfo : ArrayList<Study>? = null
    var customListView: ListView? = null
    private var myStudyListAdapter: MyStudyListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_study, container, false)

        val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

        ServerUtil.retrofitService.requestMyStudy(accessToken)
            .enqueue(object : Callback<ArrayList<Study>> {
                override fun onResponse(call: Call<ArrayList<Study>>, response: Response<ArrayList<Study>>) {
                    if (response.isSuccessful) {
                        studyInfo = response.body()!!

                        Log.d(TAG, "회원 스터디 정보 받기 성공")
                    }
                }
                override fun onFailure(call: Call<ArrayList<Study>>, t: Throwable) {
                    Log.d(TAG, "회원 스터디 정보 받기 실패")
                    Toast.makeText(getActivity(), "회원 스터디 정보 받기 실패", Toast.LENGTH_LONG).show()
                }
            })

        myStudyListAdapter = MyStudyListAdapter(context, studyInfo)
        mystudy_list.layoutManager = LinearLayoutManager(context)
        mystudy_list.adapter = myStudyListAdapter
//        myStudyListAdapter.setOnItemClickListener(object : MyStudyListAdapter.OnItemClickListener {
//            override fun onItemClick(view: View, data: Study, position: Int) {
//                var title = data.name
//                parentFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.frame_main, PostListFragment(title))
//                    .addToBackStack(null)
//                    .commit()
//            }
//        })



        return view
    }
}