package com.example.graduatedproject.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.graduatedproject.Activity.StudyApplyActivity
import com.example.graduatedproject.model.Study
import com.example.graduatedproject.R

class StudySearchRecyclerAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items =  ArrayList<Study>()

    companion object {
        //아이템뷰의 타입을 두가지로 나눔
        private const val TYPE_POST = 0
        private const val TYPE_LOADING = 1
    }

    // 아이템뷰에 게시물이 들어가는 경우
    inner class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var search_study_item : LinearLayout
        var home_study_name : TextView
        var home_on_off : TextView
        var search_study_cover : ImageView
        var study_tag1 : TextView
        var study_tag2 : TextView
        var study_tag3 : TextView
        var study_tag4 : TextView
        var study_tag5 : TextView
        var study_tag6 : TextView
        init {
            search_study_item = itemView.findViewById(R.id.search_study_item)
            home_study_name = itemView.findViewById(R.id.home_study_name)
            home_on_off = itemView.findViewById(R.id.home_on_off)
            search_study_cover = itemView.findViewById(R.id.search_study_cover)
            study_tag1 = itemView.findViewById(R.id.study_tag1)
            study_tag2 = itemView.findViewById(R.id.study_tag2)
            study_tag3 = itemView.findViewById(R.id.study_tag3)
            study_tag4 = itemView.findViewById(R.id.study_tag4)
            study_tag5 = itemView.findViewById(R.id.study_tag5)
            study_tag6 = itemView.findViewById(R.id.study_tag5)
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
                    .inflate(R.layout.item_study, parent, false)
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
        if(items == null){ //Replace "messages" with whatever array you are passing
            return 0
        }
        return items.size!!
    }

    // 뷰의 타입을 정해주는 곳이다.
    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
        return when (items[position].name) {
            " " -> TYPE_LOADING
            else -> TYPE_POST
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is SearchViewHolder){
            holder.home_study_name.setText(items[position].name)
            if(items[position].online == true && items[position].offline == true){
                holder.home_on_off.setText("OFFLINE/ONLINE")
            }
            else if(items[position].online == true){
                holder.home_on_off.setText("ONLINE")
            }
            else{
                holder.home_on_off.setText("OFFLINE")
            }

            if(items[position].image == null){
                Glide.with(holder.itemView.getContext())
                    .load(R.drawable.background_button)
                    .centerCrop()
                    .into( holder.search_study_cover)
            }
            else{
                //val resourceId = context?.resources?.getIdentifier(items[position].image!!.thumbnailImage, "drawable", context!!.packageName)

                Glide.with(holder.itemView.getContext())
                    .load(items[position].image!!.profileImage)
                    .centerCrop()
                    .into( holder.search_study_cover)
            }

            if(items!![position].studyTags != null){
                for(i in 0..items[position].studyTags!!.size-1){
                    when(i){
                        0 ->{
                            holder.study_tag1.text = items[position].studyTags!![0]
                            holder.study_tag1.visibility = View.VISIBLE
                        }
                        1 ->{
                            holder.study_tag2.text = items[position].studyTags!![1]
                            holder.study_tag2.visibility = View.VISIBLE
                        }
                        2 ->{
                            holder.study_tag3.text = items[position].studyTags!![2]
                            holder.study_tag3.visibility = View.VISIBLE
                        }
                        3 ->{
                            holder.study_tag4.text = items[position].studyTags!![3]
                            holder.study_tag4.visibility = View.VISIBLE
                        }
                        4 -> {
                            holder.study_tag5.text = items[position].studyTags!![4]
                            holder.study_tag5.visibility = View.VISIBLE
                        }
                        5 ->{
                            holder.study_tag6.text = items[position].studyTags!![5]
                            holder.study_tag6.visibility = View.VISIBLE
                        }
                    }
                }
            }
            else{}


            holder.search_study_item.setOnClickListener {
                for(i in 0 .. items.size-1){
                    if(holder.home_study_name.text == items[i].name){
                        val studyId = items[i].id
                        Log.d("studyId", studyId.toString())

                        moveDetail(studyId)


                    }
                }
            }

        }
        else{}

    }

    private fun moveDetail(studyId: Int) {
        val intent : Intent = Intent(context, StudyApplyActivity::class.java)
        intent.putExtra("studyId",studyId)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

    }

    fun setList(notice: MutableList<Study>) {
        items.addAll(notice)
        items.add(Study(0, " ", 0, 0, " ", null, null, null, null,null,null,null,null)) // progress bar 넣을 자리
    }

    fun deleteLoading() {
        items.removeAt(items.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
    }
}