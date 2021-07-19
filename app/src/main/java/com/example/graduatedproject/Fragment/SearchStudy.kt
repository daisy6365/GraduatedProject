package com.example.graduatedproject.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.graduatedproject.Model.Category
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import kotlinx.android.synthetic.main.fragment_search_study.view.*
import org.jetbrains.anko.find
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

        getCategoryList()
        spinnerAdapterparent = ArrayAdapter(requireContext(),R.layout.item_spinner, categoryParent)
        view.search_big_category.adapter = spinnerAdapterparent
        spinnerAdapterchild = ArrayAdapter(requireContext(),R.layout.item_spinner, categoryChild)
        view.search_small_category.adapter = spinnerAdapterchild

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var search_big_category : Spinner = view.findViewById(R.id.search_big_category)
        var search_small_category : Spinner = view.findViewById(R.id.search_small_category)
        var edit_search : EditText = view.findViewById(R.id.edit_search)
        var search_check_offline :CheckBox = view.findViewById(R.id.search_check_offline)
        var search_check_online : CheckBox = view.findViewById(R.id.search_check_online)
        var study_search_btn : LinearLayout = view.findViewById(R.id.study_search_btn)

        //카테고리 기능 추가

        search_big_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(search_big_category.getItemAtPosition(position).equals("큰 카테고리")){
                }
                else{
                    categoryparentSeletedItem = categoryParent!![position]
                    Log.d("categoryparentSeletedItem", categoryparentSeletedItem!!.toString())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        search_small_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(search_small_category.getItemAtPosition(position).equals("작은 카테고리")) {
                }
                else{
                    categorychildSeletedItem = categoryChild!![position]
                    Log.d("categorychildSeletedItem", categorychildSeletedItem!!.toString())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        study_search_btn.setOnClickListener {

        }
    }

    fun getCategoryList(){
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
        ServerUtil.retrofitService.requestCategoryChild()
            .enqueue(object : Callback<ArrayList<Category>> {
                override fun onResponse(call: Call<ArrayList<Category>>, response: Response<ArrayList<Category>>) {
                    if (response.isSuccessful) {
                        categoryListChild = response.body()!!
                        for(i in 0..categoryListChild!!.size-1){
                            categoryChild?.add(categoryListChild!!.get(i).name)
                        }
                        Log.d(TAG, "카테고리 자식아이템리스트 받기 성공")

                    }
                }
                override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                    Log.d(TAG, "카테고리 자식아이템리스트 받기 실패")
                    Toast.makeText(getActivity(), "카테고리 자식아이템리스트 받기 실패", Toast.LENGTH_LONG).show()
                }
            })
    }
    fun getInstance(): SearchStudy {
        return SearchStudy()
    }
}