package com.example.harudemo.todo.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.databinding.FragmentTodoListSectionBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.types.Section

class TodoListAdapter :
    RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder>() {
    var completed: Boolean = false
    var sections: List<Section> = listOf()
        set(value) {
            val newer = value.filter { it.todos.isNotEmpty() }

            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                val older = field

                override fun getOldListSize(): Int {
                    return older.size
                }

                override fun getNewListSize(): Int {
                    return newer.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return older[oldItemPosition].title == newer[newItemPosition].title
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    // TODO: 아래 항목이 적용이 되려면, Section 내의 todoList가 변경이 될시, todoList가 변경되어야 함.
                    return older[oldItemPosition] == newer[newItemPosition]
                }
            })
            field = newer
            result.dispatchUpdatesTo(this)
        }

    inner class TodoListViewHolder(private val itemBinding: FragmentTodoListSectionBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bindItem(section: Section, position: Int) {
            // Section 이름, 하위 Recycler View에 Adapter, LayoutManager 설정
            val sectionAdapter = TodoListSectionAdapter(position, completed)
            sectionAdapter.section = section
            itemBinding.tvSectionName.text = section.title
            itemBinding.rvTodoList.adapter = sectionAdapter
            itemBinding.rvTodoList.layoutManager = LinearLayoutManager(
                itemBinding.root.context, LinearLayoutManager.VERTICAL, false
            )
            itemBinding.vSectionDivider.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(TodoListFragment.COLORS[position % TodoListFragment.COLORS.size]))

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
        holder.bindItem(sections[position], position)
    }

    override fun getItemCount(): Int {
        return sections.size
    }
}