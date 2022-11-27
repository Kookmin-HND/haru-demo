package com.example.harudemo.sns.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.databinding.LayoutSnsFriendsItemBinding
import com.example.harudemo.model.SnsPost
import com.example.harudemo.sns.SnsPostDetailActivity
import kotlinx.android.synthetic.main.sns_post_recyclerview_item.view.*

class SnsFriendsRecyclerViewAdapter : RecyclerView.Adapter<SnsFriendsRecyclerViewAdapter.SnsFriendsitemViewHolder>(){

    var snsFriendsList = ArrayList<SnsPost>()

    inner class SnsFriendsitemViewHolder(private val binding: LayoutSnsFriendsItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(postData:SnsPost){
            binding.snsFiriendsWriterName.text = postData.writer
            binding.snsFriendsAverage.text = postData.average.toString()

            Glide.with(App.instance)
                .load(postData.writerPhoto)
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(binding.snsFriendsImage)
        }
    }

    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsFriendsitemViewHolder {
        val binding = LayoutSnsFriendsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SnsFriendsitemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return this.snsFriendsList.size
    }

    fun submitList(snsFriendsList: ArrayList<SnsPost>) {
        this.snsFriendsList = snsFriendsList
    }

    override fun onBindViewHolder(holder: SnsFriendsitemViewHolder, position: Int) {
        holder.bind(snsFriendsList[position])
    }
}