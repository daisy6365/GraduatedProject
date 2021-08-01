package com.example.graduatedproject.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.graduatedproject.Model.Profile
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberListAdapter(var context: Context?,
                        var memberInfo: MutableList<Profile>,
                        var accessToken : String,
                        var studyId : Int) : BaseAdapter() {

    override fun getCount(): Int {
        return memberInfo.size
    }

    override fun getItem(position: Int): Any {
        return memberInfo.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_member, parent, false)

        val member_img : ImageView = view.findViewById(R.id.member_img)
        val member_name : TextView = view.findViewById(R.id.member_name)
        val member_out : ImageView = view.findViewById(R.id.member_out)

        Glide.with(context!!)
            .load(memberInfo[position].image.thumbnailImage)
            .override(55,55)
            .centerCrop()
            .into(member_img)

        //val resourceId = context?.resources?.getIdentifier(memberInfo[position].image.thumbnailImage, "drawable", context!!.packageName)

        //member_img.setImageResource(resourceId!!)
        member_name.setText(memberInfo[position].nickName)
        member_out.setOnClickListener {
            if(position>0){
                val userId = memberInfo[position].userId

                ServerUtil.retrofitService.requestMemberDelete(accessToken,studyId,userId)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("MemberlistAdapter", "스터디멤버 삭제 성공")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.d("MemberlistAdapter", "스터디멤버 삭제 실패")
                        }
                    })

                //adapter 리스트에서 삭제
                //adapter 리스트 갱신
                memberInfo.removeAt(position)
                notifyDataSetChanged()
            }
        }
        return view
    }
}
