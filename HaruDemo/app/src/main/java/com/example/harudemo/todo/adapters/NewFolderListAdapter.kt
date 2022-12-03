package com.example.harudemo.todo.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentTodoFolderItemBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.TodoData
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.User

class NewFolderListAdapter(private val activity: FragmentActivity) :
    ListAdapter<Pair<String, Int>, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<Pair<String, Int>>() {
        override fun areItemsTheSame(oldItem: Pair<String, Int>, newItem: Pair<String, Int>): Boolean {
            // areItemsTheSame => true 이여야만 areContentsTheSame도 확인함
            // 이 어댑터는 아이템이 같은지는 별로 중요하지 않기에 항상 true를 리턴
            // return oldItem === new Item
            return true
        }

        override fun areContentsTheSame(oldItem: Pair<String, Int>, newItem: Pair<String, Int>): Boolean {
            return oldItem == newItem
        }
    }) {

    inner class ViewHolder(private val binding: FragmentTodoFolderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(pair: Pair<String, Int>) {
            binding.tvFolderTitle.text = pair.first
            binding.tvCount.text = pair.second.toString()
            binding.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("by", "folder")
                bundle.putString("folder-title", pair.first)

                // Folder 클릭시에 TodoList에 폴더로부터 클릭 됬음을 알리면서 Fragment 전환
                TodoListFragment.instance.arguments = bundle
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, TodoListFragment.instance).commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            FragmentTodoFolderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewFolderListAdapter.ViewHolder) {
            val pair = getItem(position)
            holder.bindItem(pair)
        }
    }

    fun fetchData() {
        TodoData.API.getFoldersAndCount(User.info.email, {
            submitList(it)
        }, {
            CustomToast.makeText(activity, "목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        })
    }
}