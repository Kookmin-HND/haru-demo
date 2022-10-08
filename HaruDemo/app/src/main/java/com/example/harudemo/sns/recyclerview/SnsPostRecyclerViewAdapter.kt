package com.example.harudemo.sns.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsPost
import com.example.harudemo.sns.SnsAddPostActivity
import com.example.harudemo.sns.SnsPostDetailActivity

//SnsPost 리사이클러뷰 어댑터
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

        // 리사이클러뷰에서 아이템 클릭하면 해당 게시물을 보여주는 액티비티 생성
        holder.itemView.setOnClickListener{
            //아이템을 클릭하면 해당 아이템의 데이터를 받아서 새로운 액티비티 생성
            val intent = Intent(App.instance, SnsPostDetailActivity::class.java)
            intent.putExtra("sns_post_writer", this.snsPostList[position].writer)
            intent.putExtra("sns_post_content", this.snsPostList[position].content)
            //액티비티가 아닌 곳에서 새로운 액티비티 생성하기 위해 추가
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.instance.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return this.snsPostList.size
    }

    fun submitList(snsPostList: ArrayList<SnsPost>){
        this.snsPostList = snsPostList
    }
}