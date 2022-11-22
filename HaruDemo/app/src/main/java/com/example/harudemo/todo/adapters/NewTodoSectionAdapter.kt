package com.example.harudemo.todo.adapters

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.databinding.FragmentTodoListItemBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.TodoData
import com.example.harudemo.todo.TodoInputActivity
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.TodoLog
import com.example.harudemo.utils.CustomToast
import java.time.LocalDate
import java.util.*
import kotlin.concurrent.schedule

class NewTodoSectionAdapter(private val sectionIndex: Int, private val isDateSection: Boolean) :
    ListAdapter<Pair<Todo, List<TodoLog>>, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<Pair<Todo, List<TodoLog>>>() {
        override fun areItemsTheSame(
            oldItem: Pair<Todo, List<TodoLog>>,
            newItem: Pair<Todo, List<TodoLog>>
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Pair<Todo, List<TodoLog>>,
            newItem: Pair<Todo, List<TodoLog>>
        ): Boolean {
            return oldItem == newItem
        }

    }) {
    private val days = arrayListOf(" ", "일", "월", "화", "수", "목", "금", "토")

    inner class ViewHolder(private val binding: FragmentTodoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnCheckTodo.buttonTintList =
                ColorStateList.valueOf(Color.parseColor(TodoListFragment.COLORS[sectionIndex % TodoListFragment.COLORS.size]))
        }

        fun bindItem(pair: Pair<Todo, List<TodoLog>>) {
            binding.btnCheckTodo.isChecked = pair.second.first().completed
            if (pair.second.first().completed) {
                binding.tvTodoContent.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            binding.tvTodoContent.text = pair.first.content

            var duration = " - 매주"
            for (i in 0 until pair.first.days.size) {
                if (pair.first.days[i]) {
                    duration += " ${days[i + 1]},"
                }
            }
            if (duration == " - 매주") {
                duration = " "
            }

            val dateToken = pair.second.first().date.split('-').map { it.toInt() }
            val date = LocalDate.of(dateToken[0], dateToken[1], dateToken[2])
            var index = date.dayOfWeek.value + 1
            val folder = if (isDateSection) " - #${pair.first.folder}" else ""
            if (index == 8) index = 1
            binding.tvTodoDate.text =
                "$date (${days[index]}) ${duration.slice(0 until duration.length - 1)}" +
                        "$folder"

            binding.root.setOnClickListener {
                if (pair.second.first().completed) {
                    return@setOnClickListener
                }

                val intent = Intent(it.context, TodoInputActivity::class.java)
                intent.putExtra("update", true)
                intent.putExtra("todo", pair)
                it.context.startActivity(intent)
            }

            binding.btnCheckTodo.setOnClickListener {
                if (pair.second.first().completed && pair.second.first().date != LocalDate.now()
                        .toString()
                ) {
                    binding.btnCheckTodo.isChecked = true
                    CustomToast.makeText(
                        binding.root.context,
                        "날짜가 오늘인 것만 취소할 수 있습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                TodoData.API.checkTodo(
                    pair.first.id,
                    pair.second.first().date,
                    !pair.second.first().completed,
                    {
                        Timer("complete_todo").schedule(1000) {
                            updateItem(
                                bindingAdapterPosition, Pair(
                                    pair.first,
                                    pair.second.filter { log -> log != pair.second.first() }
                                )
                            )
                        }
                    }
                )
            }

            binding.btnDelete.setOnClickListener {
                TodoData.API.delete(pair.first.id, {
                    removeItem(bindingAdapterPosition)
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            FragmentTodoListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewTodoSectionAdapter.ViewHolder) {
            val pair = getItem(position)
            if (pair.second.isNotEmpty()) {
                holder.bindItem(pair)
            } else {
                removeItem(position)
                if (currentList.none { it.second.isNotEmpty() }) {
                    TodoListFragment.instance.todoListAdapter.removeItem(sectionIndex)
                }
            }
        }
    }

    fun removeItem(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
    }

    fun updateItem(position: Int, pair: Pair<Todo, List<TodoLog>>) {
        val newList = currentList.toMutableList()
        newList[position] = pair
        submitList(newList)
    }
}