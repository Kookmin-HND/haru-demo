package com.example.harudemo.sns.recyclerview

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import kotlinx.android.synthetic.main.sns_post_image_item.view.*

//SnsPost 리사이클러뷰 어댑터
class SnsPostImagesRecyclerViewAdapter(private val items: ArrayList<Uri>, val context:Context) : RecyclerView.Adapter<SnsPostImagesRecyclerViewAdapter.ViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.sns_post_image_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindWidthView(items[position])
    }

    // 뷰홀더
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val snsImageItem = itemView.sns_post_upload_image
        fun bindWidthView(imageUri : Uri){
            //이미지 생성
            Glide.with(App.instance)
                .load(imageUri)
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(snsImageItem)
        }
    }
}