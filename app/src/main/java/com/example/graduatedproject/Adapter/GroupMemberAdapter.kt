package com.example.graduatedproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.model.Profile
import com.example.graduatedproject.R

class GroupMemberAdapter (
    var groupMemberInfo: ArrayList<Profile>?
) : RecyclerView.Adapter<GroupMemberAdapter.GroupMemberViewHolder>() {

    class GroupMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var group_member_name: TextView

        init {
            group_member_name = itemView.findViewById(R.id.group_member_name)

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupMemberViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_group_member, parent, false)

        return GroupMemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupMemberViewHolder, position: Int) {
        holder.group_member_name.text = groupMemberInfo!![position].nickName
    }

    override fun getItemCount(): Int {
        if(groupMemberInfo == null){ //Replace "messages" with whatever array you are passing
            return 0
        }
        return groupMemberInfo!!.size
    }

}