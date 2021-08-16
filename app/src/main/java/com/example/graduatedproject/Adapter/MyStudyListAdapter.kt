package com.example.graduatedproject.Adapter

import com.example.graduatedproject.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.graduatedproject.Model.Study


class MyStudyListAdapter (
    var context: Context?,
    var studyInfo: ArrayList<Study>?
) : RecyclerView.Adapter<MyStudyListAdapter.MyStudyListViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(view: View, data: Study, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStudyListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_study, parent, false)

        return MyStudyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyStudyListViewHolder, position: Int) {

        holder.home_study_name.text = studyInfo!![position].name
        if(studyInfo!![position].online == true && studyInfo!![position].offline == true){
            holder.home_on_off.setText("OFFLINE/ONLINE")
        }
        else if(studyInfo!![position].online == true){
            holder.home_on_off.setText("ONLINE")
        }
        else{
            holder.home_on_off.setText("OFFLINE")
        }
        if(studyInfo!![position].image == null){
            Glide.with(holder.itemView.getContext())
                .load(R.drawable.background_button)
                .override(140, 140)
                .centerCrop()
                .into( holder.search_study_cover)
        }
        else{
            Glide.with(holder.itemView.getContext())
                .load(studyInfo!![position].image!!.thumbnailImage)
                .override(140, 140)
                .centerCrop()
                .into( holder.search_study_cover)
        }
        for(i in 0..studyInfo!![position].studyTags!!.size-1){
            when(i){
                0 ->{
                    holder.study_tag1.text = studyInfo!![position].studyTags!![0]
                    holder.study_tag1.visibility = View.VISIBLE
                }
                1 ->{
                    holder.study_tag2.text = studyInfo!![position].studyTags!![1]
                    holder.study_tag2.visibility = View.VISIBLE
                }
                2 ->{
                    holder.study_tag3.text = studyInfo!![position].studyTags!![2]
                    holder.study_tag3.visibility = View.VISIBLE
                }
                3 ->{
                    holder.study_tag4.text = studyInfo!![position].studyTags!![3]
                    holder.study_tag4.visibility = View.VISIBLE
                }
                4 -> {
                    holder.study_tag5.text = studyInfo!![position].studyTags!![4]
                    holder.study_tag5.visibility = View.VISIBLE
                }
                5 ->{
                    holder.study_tag6.text = studyInfo!![position].studyTags!![5]
                    holder.study_tag6.visibility = View.VISIBLE
                }
            }

        }

        if(position!= RecyclerView.NO_POSITION)
        {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView,studyInfo!![position],position)
            }
        }
    }

    override fun getItemCount(): Int {
        return studyInfo!!.size
    }

    class MyStudyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var home_study_name: TextView
        var home_on_off : TextView
        var search_study_cover: ImageView
        var study_tag1 : TextView
        var study_tag2 : TextView
        var study_tag3 : TextView
        var study_tag4 : TextView
        var study_tag5 : TextView
        var study_tag6 : TextView

        init {
            home_study_name = itemView.findViewById(R.id.home_study_name)
            home_on_off = itemView.findViewById(R.id.home_on_off)
            search_study_cover = itemView.findViewById(R.id.search_study_cover)
            study_tag1 = itemView.findViewById(R.id.study_tag1)
            study_tag2 = itemView.findViewById(R.id.study_tag2)
            study_tag3 = itemView.findViewById(R.id.study_tag3)
            study_tag4 = itemView.findViewById(R.id.study_tag4)
            study_tag5 = itemView.findViewById(R.id.study_tag5)
            study_tag6 = itemView.findViewById(R.id.study_tag6)

        }

    }
}