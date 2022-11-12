package com.example.harudemo.sns.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsComment
import com.example.harudemo.model.SnsPost
import kotlinx.android.synthetic.main.layout_sns_post_item.view.*
import kotlinx.android.synthetic.main.sns_comment_item.view.*

//Sns 게시물 리사이클러뷰를 위한 뷰홀더
class SnsCommentItemViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
    private val snsCommentWriterPhoto = itemView.sns_comment_writer_photo
    private val snsCommentWriterName = itemView.sns_comment_writer_name
    private val snsCommentCreatedAt = itemView.sns_comment_created_at
    private val snsCommentContent = itemView.sns_comment_content


    fun bindWidthView(snsCommentItem : SnsComment){
        snsCommentWriterName.text = snsCommentItem.writer
        snsCommentCreatedAt.text = snsCommentItem.createdAt
        snsCommentContent.text = snsCommentItem.content

        //이미지 생성
        Glide.with(App.instance)
            .load(snsCommentItem.writerPhoto)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(snsCommentWriterPhoto)
    }
}