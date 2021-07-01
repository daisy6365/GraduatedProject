package com.example.graduatedproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Model.Content
import com.example.graduatedproject.Model.Likesearch
import com.example.graduatedproject.R

class SearchRecyclerAdapter(
    var searchList: Likesearch?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = ArrayList<Content>()

    companion object {

        //아이템뷰의 타입을 두가지로 나눔
        private const val TYPE_POST = 0
        private const val TYPE_LOADING = 1
    }

    // 아이템뷰에 게시물이 들어가는 경우
    inner class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var liketopicsearch_item : TextView

        init {
            liketopicsearch_item = itemView.findViewById(R.id.liketopicsearch_item)
        }
    }

    // 아이템뷰에 프로그레스바가 들어가는 경우
    inner class LoadingViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var item_loading : ProgressBar

        init {
            item_loading = itemView.findViewById(R.id.item_loading)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            //
            TYPE_POST -> {
                val inflatedView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_liketopicsearch, parent, false)
                return SearchViewHolder(inflatedView)
            }

            //10개를 다보여줬을경우 로딩 아이템
            else -> {
                val inflatedView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_loading, parent, false)
                return LoadingViewHolder(inflatedView)
            }
        }
    }

    override fun getItemCount(): Int {
        return searchList!!.content.size
    }

    // 뷰의 타입을 정해주는 곳이다.
    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
        return when (searchList!!.content[position].name) {
            " " -> TYPE_LOADING
            else -> TYPE_POST
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is SearchViewHolder){
            holder.liketopicsearch_item.setText(searchList!!.content.get(position).name)
        }else{

        }
    }

    fun setList(notice: MutableList<Content>) {
        items.addAll(notice)
        items.add(Content(0 ," ")) // progress bar 넣을 자리
    }

    fun deleteLoading(){
        items.removeAt(items.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
    }
}