package com.example.harudemo.todo.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentTodoFolderItemBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.TodoData
import com.example.harudemo.todo.types.Todo

class TodoFolderListAdapter(private val activity: FragmentActivity) :
    RecyclerView.Adapter<TodoFolderListAdapter.TodoFolderListViewHolder>() {
    inner class TodoFolderListViewHolder(
        private val itembinding: FragmentTodoFolderItemBinding,
        private val activity: FragmentActivity
    ) : RecyclerView.ViewHolder(itembinding.root) {
        fun bindItem(folderTitle: String) {
            // TodoFragment내 Folder RecyclerView에 폴더 이름을 기준으로 폴더 클릭 할 수 있는 아이템 생성
            itembinding.tvFolderTitle.text = folderTitle
            itembinding.tvFolderTitle.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("by", "folder")
                bundle.putString("folder-title", folderTitle)

                // Folder 클릭시에 TodoList에 폴더로부터 클릭 됬음을 알리면서 Fragment 전환
                TodoListFragment.instance.arguments = bundle
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, TodoListFragment.instance).commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoFolderListViewHolder {
        return TodoFolderListViewHolder(
            FragmentTodoFolderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), activity
        )
    }

    override fun onBindViewHolder(holder: TodoFolderListViewHolder, position: Int) {
        val folderNames = ArrayList(TodoData.todosByFolder.keys)
        folderNames.sort()
        holder.bindItem(folderNames[position])
    }

    override fun getItemCount(): Int {
        return TodoData.todosByFolder.keys.size
    }
}