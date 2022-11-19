package com.example.harudemo.sns.recyclerview

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsComment
import com.example.harudemo.model.SnsImage
import com.example.harudemo.sns.recyclerview.SnsPostImageItemViewHolder


//SnsImage 리사이클러뷰 어댑터
class SnsPostImageViewPagerAdpater : RecyclerView.Adapter<SnsPostImageItemViewHolder>() {
    private var snsPostImagesList = ArrayList<SnsImage>()

    var onItemClick : ((SnsComment) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsPostImageItemViewHolder {
        return SnsPostImageItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.sns_post_image_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SnsPostImageItemViewHolder, position: Int) {
        holder.bindWidthView(this.snsPostImagesList[position])
    }

    override fun getItemCount(): Int {
        return this.snsPostImagesList.size
    }

    fun submitList(snsImageList: ArrayList<SnsImage>){
        this.snsPostImagesList = snsImageList
    }
}