package com.example.harudemo.todo.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.databinding.FragmentTodoListSectionBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.types.Section
import kotlinx.android.synthetic.main.activity_sns_add_post.view.*

class TodoListAdapter :
    RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {
    inner class TodoListViewHolder(private val itemBinding: FragmentTodoListSectionBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bindItem(section: Section) {
            // Section 이름, 하위 Recycler View에 Adapter, LayoutManager 설정
            val sectionAdapter = TodoListSectionAdapter(section)
            itemBinding.tvSectionName.text = section.sectionTitle
            itemBinding.rvTodoList.adapter = sectionAdapter
            itemBinding.rvTodoList.layoutManager = LinearLayoutManager(
                itemBinding.root.context, LinearLayoutManager.VERTICAL, false
            )

            //스와이프 함수 호출
            val swipeHelperCallback = SwipeHelperCallback().apply {
                setClamp(200f)
            }
            val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
            itemTouchHelper.attachToRecyclerView(itemBinding.rvTodoList)

            itemBinding.rvTodoList.apply {
                setOnTouchListener { _, _ ->
                    swipeHelperCallback.removePreviousClamp(this)
                    false
                }
            }

            // Section 클릭시에 표시 전환
            itemBinding.layoutSectionTitle.setOnClickListener {
                setToggle()
            }
        }

        // 표시 전환하는 함수
        private fun setToggle() {
            if (itemBinding.rvTodoList.visibility == View.VISIBLE) {
                itemBinding.rvTodoList.visibility = View.GONE
                itemBinding.ivToggleUnselected.visibility = View.VISIBLE
                itemBinding.ivToggleSelected.visibility = View.GONE
            } else {
                itemBinding.rvTodoList.visibility = View.VISIBLE
                itemBinding.ivToggleUnselected.visibility = View.GONE
                itemBinding.ivToggleSelected.visibility = View.VISIBLE
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        return TodoListViewHolder(
            FragmentTodoListSectionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        holder.bindItem(TodoListFragment.instance.sections[position])
    }

    override fun getItemCount(): Int {
        return TodoListFragment.instance.sections.size
    }
}