package com.example.graduatedproject.Adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.graduatedproject.Activity.StudyApplyActivity
import com.example.graduatedproject.Activity.StudyChatActivity
import com.example.graduatedproject.Model.Group
import com.example.graduatedproject.R

import com.example.graduatedproject.databinding.FragmentStudyGroupBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class StudyGroupListAdapter (
    private var context : Context,
    var groupListInfo: ArrayList<Group>?
) : RecyclerView.Adapter<StudyGroupListAdapter.StudyGroupListViewHolder>() {
    class StudyGroupListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var home_study_name: TextView

        init {
            home_study_name = itemView.findViewById(R.id.home_study_name)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyGroupListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_grouplist, parent, false)

        return StudyGroupListViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudyGroupListViewHolder, position: Int) {

    }

    private fun moveDetail(gatheringId: Int) {
        val intent : Intent = Intent(context, StudyApplyActivity::class.java)
        intent.putExtra("gatheringId",gatheringId)
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }


    override fun getItemCount(): Int {
        if(groupListInfo == null){ //Replace "messages" with whatever array you are passing
            return 0
        }
        return groupListInfo!!.size
    }

}