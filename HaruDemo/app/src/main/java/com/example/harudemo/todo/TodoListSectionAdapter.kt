package com.example.harudemo.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.databinding.FragmentTodoListItemBinding

class TodoListSectionAdapter(private val section: Section) :
    RecyclerView.Adapter<TodoListSectionAdapter.TodoListSectionViewHolder>() {
    inner class TodoListSectionViewHolder(private val itemBinding: FragmentTodoListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(todoItem: Todo) {
            itemBinding.tvTodoContent.text = todoItem.content
            itemBinding.tvTodoDate.text = todoItem.todo.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListSectionViewHolder {
        return TodoListSectionViewHolder(
            FragmentTodoListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TodoListSectionViewHolder, position: Int) {
        holder.bindItem(section.todoList[position])
    }

    override fun getItemCount(): Int {
        return section.todoList.size
    }

}