package com.example.harudemo.todo.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentTodoFolderItemBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment

class TodoFolderListAdapter(private val folderTitles: ArrayList<String>, private val activity: FragmentActivity) :
    RecyclerView.Adapter<TodoFolderListAdapter.TodoFolderListViewHolder>() {

    inner class TodoFolderListViewHolder(private val itembinding: FragmentTodoFolderItemBinding, private val activity: FragmentActivity) :
        RecyclerView.ViewHolder(itembinding.root) {

        private lateinit var todoListFragment: TodoListFragment

        fun bindItem(folderTitle: String) {
            itembinding.tvFolderTitle.text = folderTitle
            itembinding.tvFolderTitle.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("by", "folder")
                bundle.putString("folder-title", folderTitle)

                todoListFragment = TodoListFragment.newInstance()
                todoListFragment.arguments = bundle
                activity.supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, todoListFragment).commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoFolderListViewHolder {
        return TodoFolderListViewHolder(
            FragmentTodoFolderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            activity
        )
    }

    override fun onBindViewHolder(holder: TodoFolderListViewHolder, position: Int) {
        holder.bindItem(folderTitles[position])
    }

    override fun getItemCount(): Int {
        return folderTitles.size
    }
}