package com.example.harudemo.sns.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsComment
import com.example.harudemo.model.SnsPost
import com.example.harudemo.sns.SnsAddPostActivity
import com.example.harudemo.sns.SnsPostDetailActivity

//SnsPost 리사이클러뷰 어댑터
class SnsCommentRecyclerViewAdapter : RecyclerView.Adapter<SnsCommentItemViewHolder>() {
    private var snsCommentList = ArrayList<SnsComment>()

    var onItemClick : ((SnsComment) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsCommentItemViewHolder {
        return SnsCommentItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.sns_comment_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SnsCommentItemViewHolder, position: Int) {
        holder.bindWidthView(this.snsCommentList[position])


//        // 클릭 리스너
//        // 리사이클러뷰에서 아이템 클릭하면 해당 게시물을 보여주는 액티비티 생성
//        holder.itemView.setOnClickListener{
//            //아이템을 클릭하면 해당 아이템의 데이터를 받아서 새로운 액티비티 생성
//            val intent = Intent(App.instance, SnsPostDetailActivity::class.java)
//            intent.putExtra("sns_post_id", this.snsCommentList[position].id)
//            intent.putExtra("sns_post_writer", this.snsCommentList[position].writer)
//            intent.putExtra("sns_post_content", this.snsCommentList[position].content)
//            //액티비티가 아닌 곳에서 새로운 액티비티 생성하기 위해 추가
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            App.instance.startActivity(intent)
//        }
    }

    override fun getItemCount(): Int {
        return this.snsCommentList.size
    }

    fun submitList(snsCommentList: ArrayList<SnsComment>){
        this.snsCommentList = snsCommentList
    }
}