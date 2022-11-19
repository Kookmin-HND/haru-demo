package com.example.harudemo.todo.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.databinding.FragmentTodoListSectionBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.TodoLog

class NewTodoListAdapter :
    ListAdapter<Section, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<Section>() {
        override fun areItemsTheSame(
            oldItem: Section,
            newItem: Section
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Section,
            newItem: Section
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    inner class ViewHolder(private val binding: FragmentTodoListSectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.layoutSectionTitle.setOnClickListener {
                setToggle()
            }

            //스와이프 함수 호출
            val swipeHelperCallback = SwipeHelperCallback().apply {
                setClamp(200f)
            }
            val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
            itemTouchHelper.attachToRecyclerView(binding.rvTodoList)

            binding.rvTodoList.apply {
                setOnTouchListener { _, _ ->
                    swipeHelperCallback.removePreviousClamp(this)
                    false
                }
            }

            binding.layoutSectionTitle.setOnClickListener {
                setToggle()
            }
        }

        fun bindItem(section: Section) {
            Log.d("[debug]", section.logs.toString())
            binding.tvSectionName.text = section.title
            binding.vSectionDivider.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(TodoListFragment.COLORS[bindingAdapterPosition % TodoListFragment.COLORS.size]))

            val sectionAdapter = NewTodoSectionAdapter(bindingAdapterPosition)
            val list: ArrayList<Pair<Todo, List<TodoLog>>> = arrayListOf()
            for (i in 0 until section.todos.size) {
                list.add(Pair(section.todos[i], section.logs[i]))
            }
            binding.rvTodoList.apply {
                adapter = sectionAdapter
                layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.VERTICAL, false
                )
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            sectionAdapter.submitList(list)
        }

        private fun setToggle() {
            if (binding.rvTodoList.visibility == View.VISIBLE) {
                binding.rvTodoList.visibility = View.GONE
                binding.ivToggleUnselected.visibility = View.VISIBLE
                binding.ivToggleSelected.visibility = View.GONE
            } else {
                binding.rvTodoList.visibility = View.VISIBLE
                binding.ivToggleUnselected.visibility = View.GONE
                binding.ivToggleSelected.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            FragmentTodoListSectionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewTodoListAdapter.ViewHolder) {
            val section = getItem(position)
            holder.bindItem(section)
        }
    }

    override fun submitList(list: List<Section>?) {
        super.submitList(list)
        TodoListFragment.instance.decideView(list)
    }

    fun removeItem(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
    }
}