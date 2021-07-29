package com.example.graduatedproject.Fragment

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.graduatedproject.R

import com.example.graduatedproject.Fragment.placeholder.PlaceholderContent.PlaceholderItem
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.Util.ServerUtil
import com.example.graduatedproject.databinding.ItemMemberBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MemberRecyclerAdapter(
    private var accessToken : String,
    private var studyId : Int,
    private var memberInfo: MutableList<Profile>
) : RecyclerView.Adapter<MemberRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        val member_img: ImageView = binding.memberImg
        val member_name: TextView = binding.memberName
        val member_out: ImageView = binding.memberOut
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemMemberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.also {
            Glide.with(it.context)
                .load(memberInfo[position].image.thumbnailImage)
                .into(holder.member_img)
        }
        holder.member_name.text = memberInfo[position].nickName

        holder.member_out.setOnClickListener {
            val userId = memberInfo[position].userId

            ServerUtil.retrofitService.requestMemberDelete(accessToken,studyId,userId)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d("MemberRecyclerAdapter", "멤버삭제 성공")
]
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("MemberRecyclerAdapter", "멤버삭제 실패")
                    }
                })
        }


    }

    override fun getItemCount(): Int = memberInfo.size



}