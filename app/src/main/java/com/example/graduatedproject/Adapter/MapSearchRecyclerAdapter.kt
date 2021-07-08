package com.example.graduatedproject.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Activity.LiketopicActivity
import com.example.graduatedproject.Activity.MapActivity
import com.example.graduatedproject.Model.ContentLocation
import com.example.graduatedproject.Model.LocationSearch
import com.example.graduatedproject.R

class MapSearchRecyclerAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items =  ArrayList<ContentLocation>()

        companion object {
            //아이템뷰의 타입을 두가지로 나눔
            private const val TYPE_POST = 0
            private const val TYPE_LOADING = 1
        }

        // 아이템뷰에 게시물이 들어가는 경우
        inner class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            var search_item : TextView

            init {
                search_item = itemView.findViewById(R.id.search_item)
            }
        }

        // 아이템뷰에 프로그레스바가 들어가는 경우
        inner class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
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
                        .inflate(R.layout.item_search, parent, false)
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
            return items.size!!

        }

        // 뷰의 타입을 정해주는 곳이다.
        override fun getItemViewType(position: Int): Int {
            // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
            return when (items[position].dong) {
                " " -> TYPE_LOADING
                else -> TYPE_POST
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder is SearchViewHolder){
                holder.search_item.setText(items[position].dong)
                holder.search_item.setOnClickListener {
                    for(i in 0 .. items.size-1){
                        if(holder.search_item.text == items[i].dong){
                            val locationId = items[i].id

                            moveDetail(locationId)


                        }
                    }
                }
            }
            else{}

        }

    private fun moveDetail(addTag: Int) {
        val intent : Intent = Intent(context, MapActivity::class.java)
        intent.putExtra("modify_item",addTag)
        context.startActivity(intent)

    }

        fun setList(notice: MutableList<ContentLocation>) {
            items.addAll(notice)
            items.add(ContentLocation(0 ," "," "," "," "," ",0.0,0.0," ")) // progress bar 넣을 자리
        }

        fun deleteLoading(){
            items.removeAt(items.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
        }
}