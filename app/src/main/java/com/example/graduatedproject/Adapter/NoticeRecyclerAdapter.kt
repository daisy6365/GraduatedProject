package com.example.graduatedproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.graduatedproject.R
import com.example.graduatedproject.databinding.ItemNoticeBinding
import com.example.graduatedproject.model.Notice

class NoticeRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = ArrayList<Notice>()

    companion object {
        //아이템뷰의 타입을 두가지로 나눔
        private const val TYPE_POST = 0
        private const val TYPE_LOADING = 1
    }


    inner class NoticeViewHolder(val binding : ItemNoticeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(notice: Notice){
            binding.notice = notice
        }
    }

    // 아이템뷰에 프로그레스바가 들어가는 경우
    inner class LoadingViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var item_loading : ProgressBar

        init {
            item_loading = itemView.findViewById(R.id.item_loading)
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when (viewType) {
            //
            TYPE_POST -> {
                val binding =
                    ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return NoticeViewHolder(binding)
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notice = items[position]
        if (holder is NoticeViewHolder) holder.bind(notice)
        else { }

    }

    fun setList(notice: MutableList<Notice>) {
        items.addAll(notice)
        items.add(Notice(0 , 0," "," "," ")) // progress bar 넣을 자리
    }

    fun deleteLoading() {
        items.removeAt(items.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
    }

    // 뷰의 타입을 정해주는 곳이다.
    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
        return when (items[position].title) {
            " " -> TYPE_LOADING
            else -> TYPE_POST
        }
    }

    override fun getItemCount(): Int {
        return items.size!!
    }


}