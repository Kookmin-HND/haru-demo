package com.example.harudemo.sns.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsPost
import com.example.harudemo.sns.SnsPostDetailActivity

//SnsPost 리사이클러뷰 어댑터
class SnsPostRecyclerViewAdapter : RecyclerView.Adapter<SnsPostItemViewHolder>() {
    private var snsPostList = ArrayList<SnsPost>()

    val HEADER = 1
    val ITEM = 2
    val FOOTER = 3

    var onItemClick: ((SnsPost) -> Unit)? = null

    // recyclerview header, footer, item 분리
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsPostItemViewHolder {
        val view =
            when (viewType) {
                HEADER -> LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.sns_post_recyclerview_header, parent, false)
                FOOTER -> LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.sns_post_recyclerview_footer, parent, false)
                else ->
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.sns_post_recyclerview_item, parent, false)
            }
        return SnsPostItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SnsPostItemViewHolder, position: Int) {
        if (position == 0) {
            // HEADER
        } else if (position == this.snsPostList.size + 1) {
            // FOOTER
        } else {
            //header is 0 so position -1
            holder.bindWidthView(this.snsPostList[position-1])

            // 리사이클러뷰에서 아이템 클릭하면 해당 게시물을 보여주는 액티비티 생성
            holder.itemView.setOnClickListener {
                //아이템을 클릭하면 해당 아이템의 데이터를 받아서 새로운 액티비티 생성
                val intent = Intent(App.instance, SnsPostDetailActivity::class.java)
                intent.putExtra("sns_post_id", this.snsPostList[position-1].id)
                intent.putExtra("sns_post_category", this.snsPostList[position-1].category)
                intent.putExtra("sns_post_writer", this.snsPostList[position-1].writer)
                intent.putExtra("sns_post_content", this.snsPostList[position-1].content)
                intent.putExtra("sns_post_like_list", this.snsPostList[position-1].postLikeList)
                intent.putExtra("sns_post_writer_photo", this.snsPostList[position-1].writerPhoto)
                //액티비티가 아닌 곳에서 새로운 액티비티 생성하기 위해 추가
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.instance.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return this.snsPostList.size + 2
    }

    fun submitList(snsPostList: ArrayList<SnsPost>) {
        this.snsPostList = snsPostList
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) {
            HEADER
        } else if(position == this.snsPostList.size + 1) {
            FOOTER
        } else {
            ITEM
        }
    }
}