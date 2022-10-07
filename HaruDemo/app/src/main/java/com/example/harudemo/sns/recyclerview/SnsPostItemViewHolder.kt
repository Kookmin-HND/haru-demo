package com.example.harudemo.sns.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsPost
import kotlinx.android.synthetic.main.layout_sns_post_item.view.*

//Sns 게시물 리사이클러뷰를 위한 뷰홀더
class SnsPostItemViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
    private val snsPostWriterPhoto = itemView.sns_post_writer_photo
    private val snsPostWriterName = itemView.sns_post_writer_name
    private val snsPostCreatedAt = itemView.sns_post_created_at
    private val snsPostContent = itemView.sns_post_content


    fun bindWidthView(snsPostItem : SnsPost){
        snsPostWriterName.text = snsPostItem.writer
        snsPostCreatedAt.text = snsPostItem.createdAt
        snsPostContent.text = snsPostItem.content

        //이미지 생성
        Glide.with(App.instance)
            .load(snsPostItem.writerPhoto)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(snsPostWriterPhoto)
    }
}