package com.example.harudemo.sns.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.model.SnsComment

//SnsComment 리사이클러뷰 어댑터
class SnsCommentRecyclerViewAdapter : RecyclerView.Adapter<SnsCommentItemViewHolder>() {
    private var snsCommentList = ArrayList<SnsComment>()

    var onItemClick : ((SnsComment) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsCommentItemViewHolder {
        return SnsCommentItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.sns_comment_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SnsCommentItemViewHolder, position: Int) {
        holder.bindWidthView(this.snsCommentList[position])

    }

    override fun getItemCount(): Int {
        return this.snsCommentList.size
    }

    fun submitList(snsCommentList: ArrayList<SnsComment>){
        this.snsCommentList = snsCommentList
    }
}