package com.example.harudemo.todo.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentTodoFolderItemBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.TodoData
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.TodoLog
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.User

class TodoFolderListAdapter(private val activity: FragmentActivity) :
    RecyclerView.Adapter<TodoFolderListAdapter.TodoFolderListViewHolder>() {
    private var data: ArrayList<Pair<String, Int>> = arrayListOf()

    inner class TodoFolderListViewHolder(
        private val binding: FragmentTodoFolderItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(folder: Pair<String, Int>) {
            // TodoFragment내 Folder RecyclerView에 폴더 이름을 기준으로 폴더 클릭 할 수 있는 아이템 생성
            binding.tvFolderTitle.text = folder.first
            binding.tvCount.text = folder.second.toString()
            binding.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("by", "folder")
                bundle.putString("folder-title", folder.first)

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
            )
        )
    }

    override fun onBindViewHolder(holder: TodoFolderListViewHolder, position: Int) {
        holder.bindItem(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun fetchData() {
        TodoData.API.getFoldersAndCount(User.info?.email!!, {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                val newer = it
                val older = data

                override fun getOldListSize(): Int {
                    return older.size
                }

                override fun getNewListSize(): Int {
                    return newer.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return older[oldItemPosition] === newer[newItemPosition]
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return older[oldItemPosition] == newer[newItemPosition]
                }
            })
            data = it
            result.dispatchUpdatesTo(this)
        }, {
            CustomToast.makeText(activity, "목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        })
    }
}