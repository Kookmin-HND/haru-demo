package com.example.harudemo.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.databinding.FragmentTodoListSectionBinding

class TodoListAdapter(private val sections: ArrayList<Section>) :
    RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {
    inner class TodoListViewHolder(private val itemBinding: FragmentTodoListSectionBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(section: Section) {
            val sectionAdapter = TodoListSectionAdapter(section)
            itemBinding.tvSectionName.text = section.sectionTitle
            itemBinding.rvTodoList.adapter = sectionAdapter
            itemBinding.rvTodoList.layoutManager = LinearLayoutManager(
                itemBinding.root.context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        return TodoListViewHolder(
            FragmentTodoListSectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        holder.bindItem(sections[position])
    }

    override fun getItemCount(): Int {
        return sections.size
    }


}