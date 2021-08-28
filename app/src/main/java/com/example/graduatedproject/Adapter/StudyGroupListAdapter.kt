package com.example.graduatedproject.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.graduatedproject.Activity.StudyGroupApplyActivity
import com.example.graduatedproject.Model.Group
import com.example.graduatedproject.R

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class StudyGroupListAdapter (
    private var context : Context
) : RecyclerView.Adapter<StudyGroupListAdapter.StudyGroupListViewHolder>() {
    private val groupInfo =  ArrayList<Group>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyGroupListAdapter.StudyGroupListViewHolder  {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_grouplist, parent, false)

        return StudyGroupListViewHolder(view)
    }
    class StudyGroupListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item_group : RelativeLayout
        var group_date: TextView
        var group_on_off : TextView
        var group_place : TextView
        var group_id : TextView

        init {
            group_date = itemView.findViewById(R.id.group_date)
            group_on_off = itemView.findViewById(R.id.group_on_off)
            group_place = itemView.findViewById(R.id.group_place)
            item_group = itemView.findViewById(R.id.item_group)
            group_id = itemView.findViewById(R.id.group_id)
        }
    }

    override fun onBindViewHolder(holder: StudyGroupListAdapter.StudyGroupListViewHolder, position: Int) {
        val str = groupInfo[position].gatheringTime
        val idT: Int = str!!.indexOf("T")
        val date = str.substring(0,idT)
        val time = str.substring(idT+1)

        val dateList = date.split("-")
        val date_group = dateList[0] + "년 " + dateList[1] + "월 " + dateList[2] + "일 "
        val timeList = time.split(":")
        val time_group = timeList[0] + "시 " + timeList[1] + "분"

        holder.group_date.text = date_group + time_group

        holder.group_on_off.text = groupInfo[position].shape
        holder.group_place.text = groupInfo[position].place?.name
        holder.group_id.text = groupInfo[position].id.toString()


        holder.item_group.setOnClickListener {
            for(i in 0 .. groupInfo!!.size-1){
                if(holder.group_id.text == groupInfo!![i].id.toString()){
                    val gatheringId = groupInfo[i].id
                    Log.d("studyId", gatheringId.toString())

                    moveDetail(gatheringId)
                }
            }
        }


    }

    private fun moveDetail(gatheringId: Int) {
        val intent : Intent = Intent(context, StudyGroupApplyActivity::class.java)
        intent.putExtra("gatheringId",gatheringId)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun setList(notice: MutableList<Group>) {
        groupInfo.addAll(notice)
        groupInfo.add(Group(0, 0, " ", 0, " ", " ", null, null)) // progress bar 넣을 자리
    }

    fun deleteLoading() {
        groupInfo.removeAt(groupInfo.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
    }

    override fun getItemCount(): Int {
        if(groupInfo == null){ //Replace "messages" with whatever array you are passing
            return 0
        }
        return groupInfo!!.size
    }

}