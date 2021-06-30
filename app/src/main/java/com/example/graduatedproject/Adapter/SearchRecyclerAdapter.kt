package com.example.graduatedproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Model.Likesearch
import com.example.graduatedproject.R

class SearchRecyclerAdapter(
    var searchList: Likesearch?,
    var LIST_LENGTH: Int,
    val inflater: LayoutInflater): RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {
    companion object {
        private const val TYPE_POST = 0
        private const val TYPE_LOADING = 1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //var text = postList!!.content[1].id
        var liketopicsearch_item : TextView

        init {
            liketopicsearch_item = itemView.findViewById(R.id.liketopicsearch_item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            //
            TYPE_POST -> {
                val inflatedView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_liketopicsearch, parent, false)
                return ViewHolder(inflatedView)
            }

            //10개를 다보여줬을경우 로딩 아이템
            else -> {
                val inflatedView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_loading, parent, false)
                return ViewHolder(inflatedView)
            }
        }
    }

    override fun getItemCount(): Int {
        return LIST_LENGTH
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            10 -> {
                return TYPE_LOADING
            }
            else -> {return TYPE_POST}
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.liketopicsearch_item.setText(searchList!!.content.get(position).name)
    }
}