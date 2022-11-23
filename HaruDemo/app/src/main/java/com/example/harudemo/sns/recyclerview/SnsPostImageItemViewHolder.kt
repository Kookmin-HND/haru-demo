package com.example.harudemo.sns.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsImage
import kotlinx.android.synthetic.main.sns_post_image_item.view.*

//Sns 게시물 리사이클러뷰를 위한 뷰홀더
class SnsPostImageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val snsPostUploadImage = itemView.sns_post_upload_image

    fun bindWidthView(snsImageItem: SnsImage) {
        //이미지 연결
        Glide.with(App.instance)
            .load(snsImageItem.url)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(snsPostUploadImage)
    }
}