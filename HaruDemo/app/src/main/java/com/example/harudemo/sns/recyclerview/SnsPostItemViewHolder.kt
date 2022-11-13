package com.example.harudemo.sns.recyclerview

import android.text.method.TextKeyListener.clear
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
    private val snsPostPreviewImage = itemView.sns_post_preview_image


    fun bindWidthView(snsPostItem : SnsPost){
        snsPostWriterName.text = snsPostItem.writer
        snsPostCreatedAt.text = snsPostItem.createdAt
        snsPostContent.text = snsPostItem.content


        if(snsPostItem.postImageList.isNotEmpty()){
            snsPostPreviewImage.visibility = View.VISIBLE
            Glide.with(App.instance)
                .load(snsPostItem.postImageList[0])
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(snsPostPreviewImage)
        }
        else{//이미지가 없는 게시글인경우
            snsPostPreviewImage.visibility = View.GONE
        }


        //프로필 이미지 생성
        Glide.with(App.instance)
            .load(snsPostItem.writerPhoto)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(snsPostWriterPhoto)
    }
}