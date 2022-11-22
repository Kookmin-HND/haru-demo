package com.example.harudemo.sns.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsPost
import kotlinx.android.synthetic.main.sns_post_recyclerview_item.view.*

class SnsFriendsitemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val snsFriendsWriterPhoto = itemView.sns_post_writer_photo
    private val snsFriendsWriterName = itemView.sns_post_writer_name
    private val snsFriendsAverage = itemView.sns_post_created_at

    fun bindWidthView(snsFriendsItem : SnsPost){
        snsFriendsWriterName.text = snsFriendsItem.writer
        snsFriendsAverage.text = snsFriendsItem.average.toString()

        //이미지 생성
        Glide.with(App.instance)
            .load(snsFriendsItem.writerPhoto)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(snsFriendsWriterPhoto)
    }

}