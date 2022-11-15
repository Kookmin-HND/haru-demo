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
import com.example.harudemo.utils.CustomToast

class TodoFolderListAdapter(private val activity: FragmentActivity) :
    RecyclerView.Adapter<TodoFolderListAdapter.TodoFolderListViewHolder>() {
    private var folderList: List<String> = listOf()
    private var folderMap: HashMap<String, ArrayList<Todo>> = hashMapOf()

    inner class TodoFolderListViewHolder(
        private val itembinding: FragmentTodoFolderItemBinding,
    ) : RecyclerView.ViewHolder(itembinding.root) {
        fun bindItem(folder: String) {
            // TodoFragment내 Folder RecyclerView에 폴더 이름을 기준으로 폴더 클릭 할 수 있는 아이템 생성
            itembinding.tvFolderTitle.text = folder
            itembinding.tvCount.text = folderMap[folder].toString()
            itembinding.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("by", "folder")
                bundle.putString("folder-title", folder)

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
        holder.bindItem(folderList[position])
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    fun fetchData() {
        TodoData.API.getAllTodosByFolder("cjeongmin27@gmail.com", false, {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                val newerList = it.keys.toList()
                val newerMap = it

                val olderList = folderList
                val olderMap = folderMap

                override fun getOldListSize(): Int {
                    return olderList.size
                }

                override fun getNewListSize(): Int {
                    return newerList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return olderList[oldItemPosition] == newerList[newItemPosition]
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return olderMap[olderList[oldItemPosition]]?.size == newerMap[newerList[newItemPosition]]?.size
                }
            })
            folderList = it.keys.toList()
            folderMap = it
            result.dispatchUpdatesTo(this)
        }, {
            CustomToast.makeText(activity, "목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        })
    }
}