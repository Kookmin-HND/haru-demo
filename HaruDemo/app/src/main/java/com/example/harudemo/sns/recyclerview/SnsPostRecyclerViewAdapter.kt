package com.example.harudemo.sns.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.model.SnsPost

class SnsPostRecyclerViewAdapter : RecyclerView.Adapter<SnsPostItemViewHolder>() {
    private var snsPostList = ArrayList<SnsPost>()

    var onItemClick : ((SnsPost) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsPostItemViewHolder {
        return SnsPostItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_sns_post_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SnsPostItemViewHolder, position: Int) {
        holder.bindWidthView(this.snsPostList[position])

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(this.snsPostList[position])
        }
    }

    override fun getItemCount(): Int {
        return this.snsPostList.size
    }

    fun submitList(snsPostList: ArrayList<SnsPost>){
        this.snsPostList = snsPostList
    }
}