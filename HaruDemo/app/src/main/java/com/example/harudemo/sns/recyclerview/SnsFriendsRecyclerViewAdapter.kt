package com.example.harudemo.sns.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsPost
import com.example.harudemo.sns.SnsPostDetailActivity

class SnsFriendsRecyclerViewAdapter : RecyclerView.Adapter<SnsFriendsitemViewHolder>(){

    private var snsFriendsList = ArrayList<SnsPost>()

    var onItemClick: ((SnsPost) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsFriendsitemViewHolder {
        return SnsFriendsitemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_sns_friends_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SnsFriendsitemViewHolder, position: Int) {
        holder.bindWidthView(this.snsFriendsList[position])
    }

    override fun getItemCount(): Int {
        return this.snsFriendsList.size
    }

    fun submitList(snsFriendsList: ArrayList<SnsPost>) {
        this.snsFriendsList = snsFriendsList
    }

}