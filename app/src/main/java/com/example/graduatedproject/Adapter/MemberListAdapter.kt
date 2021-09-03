package com.example.graduatedproject.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberListAdapter(
    var context: Context?,
    var memberInfo: MutableList<Profile>?,
    var accessToken : String,
    var studyId : Int
    ) : RecyclerView.Adapter<MemberListAdapter.MemberViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_member, parent, false)

        return MemberViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged", "RecyclerView")
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {

        if(memberInfo!![position].image == null){
            Glide.with(holder.itemView.getContext())
                .load(R.drawable.profile_init)
                .override(55,55)
                .centerCrop()
                .into(holder.member_img)
        }
        else{
            Glide.with(holder.itemView.getContext())
                .load(memberInfo!![position].image.thumbnailImage)
                .override(55,55)
                .centerCrop()
                .into(holder.member_img)
        }

        //val resourceId = context?.resources?.getIdentifier(memberInfo[position].image.thumbnailImage, "drawable", context!!.packageName)

        //member_img.setImageResource(resourceId!!)
        holder.member_name.setText(memberInfo!![position].nickName)
        holder.member_out.setOnClickListener {
            for(i in 0 .. memberInfo!!.size-1) {
                if (holder.member_name.text == memberInfo!![i].nickName) {
                    val userId = memberInfo!![i].userId
                    Log.d("userId", userId.toString())

                    ServerUtil.retrofitService.requestMemberDelete(accessToken,studyId,userId)
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Log.d("MemberlistAdapter", "스터디멤버 삭제 성공")

                                    //adapter 리스트에서 삭제
                                    //adapter 리스트 갱신
                                    memberInfo!!.removeAt(position)
                                    notifyDataSetChanged()
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.d("MemberlistAdapter", "스터디멤버 삭제 실패")
                            }
                        })

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return memberInfo!!.size
    }

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var member_img: ImageView
        var member_name: TextView
        var member_out: ImageView

        init {
            member_img = itemView.findViewById(R.id.member_img)
            member_name = itemView.findViewById(R.id.member_name)
            member_out = itemView.findViewById(R.id.member_out)
        }

    }

}
