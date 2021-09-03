package com.example.graduatedproject.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberAddListAdapter(
    var context: Context?,
    var memberAddInfo: MutableList<Profile>?,
    var accessToken : String,
    var studyId : Int
) : RecyclerView.Adapter<MemberAddListAdapter.MemberAddViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberAddViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_add_member, parent, false)

        return MemberAddViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged", "RecyclerView")
    override fun onBindViewHolder(holder: MemberAddViewHolder, position: Int) {

        if(memberAddInfo!![position].image == null){
            Glide.with(holder.itemView.getContext())
                .load(R.drawable.profile_init)
                .override(55,55)
                .centerCrop()
                .into(holder.member_img)
        }
        else{
            Glide.with(holder.itemView.getContext())
                .load(memberAddInfo!![position].image.thumbnailImage)
                .override(55,55)
                .centerCrop()
                .into(holder.member_img)
        }

        //val resourceId = context?.resources?.getIdentifier(memberInfo[position].image.thumbnailImage, "drawable", context!!.packageName)

        //member_img.setImageResource(resourceId!!)
        holder.member_name.setText(memberAddInfo!![position].nickName)
        holder.member_add_btn.setOnClickListener {
            for(i in 0 .. memberAddInfo!!.size-1){
                if(holder.member_name.text == memberAddInfo!![i].nickName){
                    val userId = memberAddInfo!![i].userId
                    Log.d("userId", userId.toString())

                    ServerUtil.retrofitService.requestAddMember(accessToken,studyId,userId)
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Log.d("MemberAddListAdapter", "지원멤버 추가 성공")

                                    memberAddInfo!!.removeAt(i)
                                    notifyDataSetChanged()
                                }
                            }
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.d("MemberAddListAdapter", "지원 멤버 추가 실패")
                            }
                        })

                }
            }
        }
        holder.member_delete_btn.setOnClickListener {
            for(i in 0 .. memberAddInfo!!.size-1) {
                if (holder.member_name.text == memberAddInfo!![i].nickName) {
                    val userId = memberAddInfo!![i].userId
                    Log.d("userId", userId.toString())

                    ServerUtil.retrofitService.requestAddMemberDelete(accessToken, studyId, userId)
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Log.d("MemberAddListAdapter", "지원멤버 거부 성공")

                                    //adapter 리스트에서 삭제
                                    //adapter 리스트 갱신
                                    memberAddInfo!!.removeAt(position)
                                    notifyDataSetChanged()
                                }
                            }
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.d("MemberAddListAdapter", "지원 멤버 거부 실패")
                            }
                        })
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return memberAddInfo!!.size
    }

    class MemberAddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var member_img: ImageView
        var member_name: TextView
        var member_add_btn: ImageView
        var member_delete_btn: ImageView

        init {
            member_img = itemView.findViewById(R.id.member_img)
            member_name = itemView.findViewById(R.id.member_name)
            member_add_btn = itemView.findViewById(R.id.member_add_btn)
            member_delete_btn = itemView.findViewById(R.id.member_delete_btn)
        }

    }

}
