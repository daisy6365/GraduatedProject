package com.example.graduatedproject.Fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.graduatedproject.Activity.StudySearchActivity
import com.example.graduatedproject.model.Category
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import kotlinx.android.synthetic.main.activity_study_create.*
import kotlinx.android.synthetic.main.fragment_search_study.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [SearchStudy.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchStudy : DialogFragment() {
    val TAG = SearchStudy::class.java.simpleName
    var offline : Boolean = false
    var online : Boolean = false
    private lateinit var spinnerAdapterparent : SpinnerAdapter
    private lateinit var spinnerAdapterchild : SpinnerAdapter
    var categoryListParent : ArrayList<Category>? = null
    var categoryListChild : ArrayList<Category>? = null
    var categoryParent: MutableList<String> = mutableListOf("큰 카테고리")
    var categoryChild : MutableList<String> =  mutableListOf("작은 카테고리")
    var categoryparentSeletedItem : String? = null
    var categorychildSeletedItem : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_search_study, container, false)

        //부모카테고리 조회
        ServerUtil.retrofitService.requestCategoryParent()
            .enqueue(object : Callback<ArrayList<Category>> {
                override fun onResponse(call: Call<ArrayList<Category>>, response: Response<ArrayList<Category>>) {
                    if (response.isSuccessful) {
                        categoryListParent = response.body()
                        for(i in 0 .. categoryListParent!!.size-1){
                            categoryParent?.add(categoryListParent!!.get(i).name)

                        }
                        Log.d(TAG, "카테고리 부모아이템리스트 받기 성공")
                    }
                }
                override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                    Log.d(TAG, "카테고리 부모아이템리스트 받기 실패")
                    Toast.makeText(getActivity(), "카테고리 부모아이템리스트 받기 실패", Toast.LENGTH_LONG).show()
                }
            })
        //부모 카테고리 화면에 출력
        spinnerAdapterparent = ArrayAdapter(requireContext(),R.layout.item_spinner, categoryParent)
        view.search_big_category.adapter = spinnerAdapterparent

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var search_big_category : Spinner = view.findViewById(R.id.search_big_category)
        var search_small_category : Spinner = view.findViewById(R.id.search_small_category)
        var edit_search : EditText = view.findViewById(R.id.edit_search)
        var search_check_offline :CheckBox = view.findViewById(R.id.search_check_offline)
        var search_check_online : CheckBox = view.findViewById(R.id.search_check_online)
        var study_search_btn : TextView = view.findViewById(R.id.study_search_btn)

        //카테고리 기능 추가
        search_big_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(search_big_category.getItemAtPosition(position).equals("큰 카테고리")){ }
                for(i in 1..categoryParent.size-1){
                    categoryChild =  mutableListOf("작은 카테고리")

                    spinnerAdapterchild = ArrayAdapter(requireContext(),R.layout.item_spinner, categoryChild!!)
                    search_small_category.adapter = spinnerAdapterchild

                    if(search_big_category.getItemAtPosition(position).equals(categoryParent[i])){
                        //부모카테고리값에 해당하는 자식 카테고리 조회해옴
                        ServerUtil.retrofitService.requestCategoryChild(i)
                            .enqueue(object : Callback<ArrayList<Category>> {
                                override fun onResponse(call: Call<ArrayList<Category>>, response: Response<ArrayList<Category>>) {
                                    if (response.isSuccessful) {
                                        categoryListChild = response.body()!!
                                        for(i in 0..categoryListChild!!.size-1){
                                            categoryChild?.add(categoryListChild!!.get(i).name)
                                        }
                                        Log.d(TAG, "카테고리 자식아이템리스트 받기 성공")

                                        spinnerAdapterchild = ArrayAdapter(requireContext(),R.layout.item_spinner, categoryChild!!)
                                        search_small_category.adapter = spinnerAdapterchild
                                    }
                                }
                                override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                                    Log.d(TAG, "카테고리 자식아이템리스트 받기 실패")
                                    Toast.makeText(getActivity(), "카테고리 자식아이템리스트 받기 실패", Toast.LENGTH_LONG).show()
                                }
                            })
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        search_small_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, num: Int, id: Long) {
                if(search_small_category.getItemAtPosition(num).equals("작은 카테고리")) {
                }
                else{
                    categorychildSeletedItem = categoryChild!![num]
                    Log.d("categorychildSeletedItem", categorychildSeletedItem!!.toString())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        search_check_offline.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                offline = true
            }
            else{
                offline = false
            }
        }
        search_check_online.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                online = true
            }
            else{
                online = false
            }
        }

        study_search_btn.setOnClickListener {
            //자식카테고리 id, 검색어, 온오프라인 정보 가져오기
            var categorychildSeletedId : Int? = null

            if(categorychildSeletedItem == null){
                var builder = AlertDialog.Builder(context)
                builder.setTitle("알림")
                builder.setMessage("검색할 카테고리를 정확히 입력해주세요.")
                builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which -> })
                builder.show()
            }
            else{
                for(i in 0..categoryListChild!!.size-1){
                    if(categoryListChild!!.get(i).name == categorychildSeletedItem){
                        categorychildSeletedId = categoryListChild!!.get(i).id
                    }
                }

                var searchKeyword = edit_search.text.toString()

                //intent통해서 정보 전달 및 화면 전환
                //dialogfragment 창은 닫기
                activity?.let {
                    val intent = Intent(context, StudySearchActivity::class.java)
                    intent.apply {
                        this.putExtra("categoryId",categorychildSeletedId) // 데이터 넣기
                        this.putExtra("searchKeyword",searchKeyword) // 데이터 넣기
                        this.putExtra("offline",offline) // 데이터 넣기
                        this.putExtra("online",online) // 데이터 넣기
                        dismiss()
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    }
                }
            }


        }

    }
    fun getInstance(): SearchStudy {
        return SearchStudy()
    }
}