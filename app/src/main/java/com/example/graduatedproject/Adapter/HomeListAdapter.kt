package com.example.graduatedproject.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.graduatedproject.Activity.StudyApplyActivity
import com.example.graduatedproject.model.Study
import com.example.graduatedproject.R

class HomeListAdapter (
    private var context : Context
) : RecyclerView.Adapter<HomeListAdapter.HomeListViewHolder>() {
    private val studyInfo =  ArrayList<Study>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_study, parent, false)

        return HomeListViewHolder(view)
    }
    class HomeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val search_study_item : LinearLayout
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
            search_study_item = itemView.findViewById(R.id.search_study_item)
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
    override fun onBindViewHolder(holder: HomeListViewHolder, position: Int) {
        holder.home_study_name.text = studyInfo!![position].name
        holder.search_study_cover.clipToOutline = true

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
                .load(R.drawable.applogo_gray)
                .centerCrop()
                .into( holder.search_study_cover)
        }
        else{
            Glide.with(holder.itemView.getContext())
                .load(studyInfo!![position].image!!.profileImage)
                .centerCrop()
                .into( holder.search_study_cover)
        }


        if(studyInfo!![position].studyTags != null){
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
        }
        else{}
        holder.search_study_item.setOnClickListener {
            for(i in 0 .. studyInfo!!.size-1){
                if(holder.home_study_name.text == studyInfo!![i].name){
                    val studyId = studyInfo!![i].id
                    Log.d("studyId", studyId.toString())

                    moveDetail(studyId)


                }
            }
        }
    }

    private fun moveDetail(studyId: Int) {
        val intent : Intent = Intent(context, StudyApplyActivity::class.java)
        intent.putExtra("studyId",studyId)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun setList(notice: MutableList<Study>) {
        studyInfo.addAll(notice)
        studyInfo.add(Study(0, " ", 0, 0, " ", null, null, null, null,null,null,null,null)) // progress bar 넣을 자리
    }

    fun deleteLoading() {
        studyInfo.removeAt(studyInfo.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
    }

    override fun getItemCount(): Int {
        if(studyInfo == null){ //Replace "messages" with whatever array you are passing
            return 0
        }
        return studyInfo!!.size
    }
}