package com.example.graduatedproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.Model.ApplyStudy
import com.example.graduatedproject.R

class StudyApplyStatusAdapter (
    var studyApplyList: ArrayList<ApplyStudy>?
) : RecyclerView.Adapter<StudyApplyStatusAdapter.StudyApplyStatusViewHolder>() {

    class StudyApplyStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var applystudy_name: TextView
        val status_refuse : LinearLayout
        val status_accept : LinearLayout
        val status_wait : LinearLayout

        init {
            applystudy_name = itemView.findViewById(R.id.applystudy_name)
            status_refuse = itemView.findViewById(R.id.status_refuse)
            status_accept = itemView.findViewById(R.id.status_accept)
            status_wait  = itemView.findViewById(R.id.status_wait)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudyApplyStatusViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_applystatus, parent, false)

        return StudyApplyStatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudyApplyStatusViewHolder, position: Int) {
        holder.applystudy_name.text = studyApplyList!![position].studyName
        if(studyApplyList!![position].status == "FAIL"){
            holder.status_wait.visibility = View.GONE
            holder.status_refuse.visibility = View.VISIBLE
        }
        else if(studyApplyList!![position].status == "SUCCESS"){
            holder.status_wait.visibility = View.GONE
            holder.status_accept.visibility = View.VISIBLE
        }
        else{}
    }

    override fun getItemCount(): Int {
        if(studyApplyList == null){ //Replace "messages" with whatever array you are passing
            return 0
        }
        return studyApplyList!!.size
    }

}