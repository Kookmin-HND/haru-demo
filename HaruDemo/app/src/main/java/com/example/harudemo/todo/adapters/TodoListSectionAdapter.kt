package com.example.harudemo.todo.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentTodoListItemBinding
import com.example.harudemo.fragments.TodoFragment
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.TodoData
import com.example.harudemo.todo.TodoInputActivity
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import java.security.PrivateKey
import java.time.LocalDate
import java.util.*
import kotlin.concurrent.schedule


class TodoListSectionAdapter(
    private val section: Section,
    private val colorPosition: Int,
) : RecyclerView.Adapter<TodoListSectionAdapter.TodoListSectionViewHolder>() {
    inner class TodoListSectionViewHolder(private val itemBinding: FragmentTodoListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private val days = arrayListOf("월", "화", "수", "목", "금", "토", "일")

        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bindItem(todo: Todo, position: Int) {
            // Section으로부터 받은 Todo를 단순히 데이터 삽입
            itemBinding.btnCheckTodo.buttonTintList =
                ColorStateList.valueOf(Color.parseColor(TodoListFragment.COLORS[colorPosition % TodoListFragment.COLORS.size]))

            if (todo.completed) {
                itemBinding.btnCheckTodo.isChecked = true
                itemBinding.tvTodoContent.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            itemBinding.tvTodoContent.text = todo.content

            val dateToken = todo.date.split('-').map { it.toInt() }
            val date = LocalDate.of(dateToken[0], dateToken[1], dateToken[2])
            itemBinding.tvTodoDate.text = "$date (${days[date.dayOfWeek.value - 1]})"

            // Update 실행 Action
            itemBinding.root.setOnClickListener {
                if (todo.completed) {
                    return@setOnClickListener
                }

                val intent = Intent(it.context, TodoInputActivity::class.java)
                intent.putExtra("update", true)
                intent.putExtra("todo", todo)
                it.context.startActivity(intent)
            }

            // completed Toggle Action
            val completed = todo.completed
            itemBinding.btnCheckTodo.setOnClickListener {
                if (!todo.completed == completed)
                    return@setOnClickListener

                TodoData.API.update(
                    todo.id,
                    todo.folder,
                    todo.content,
                    todo.date,
                    !todo.completed,
                    {
                        if (!todo.completed) {
                            itemBinding.btnCheckTodo.isChecked = true
                            itemBinding.tvTodoContent.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                        } else {
                            itemBinding.btnCheckTodo.isChecked = false
                            itemBinding.tvTodoContent.paintFlags = Paint.LINEAR_TEXT_FLAG
                        }
                        Timer("completed_item", true).schedule(1000) {
                            TodoListFragment.instance.activity?.runOnUiThread {
                                TodoData.update(todo, completed = !todo.completed)
                                val index = section.todoList.indexOf(todo)
                                section.todoList.remove(todo)
                                notifyItemRemoved(index)
                                if (section.todoList.isEmpty()) {
                                    TodoFragment.folderListAdapter.notifyDataSetChanged()
                                    val sectionIndex =
                                        TodoListFragment.instance.sections.indexOf(section)
                                    TodoListFragment.instance.sections =
                                        TodoListFragment.instance.sections.filter {
                                            it != section
                                        }
                                    TodoListFragment.instance.todoListAdapter?.notifyItemRemoved(
                                        sectionIndex
                                    )
                                    if (TodoListFragment.instance.sections.isEmpty()) {
                                        TodoListFragment.instance.refreshView()
                                    }
                                }
                            }
                        }
                    })
            }

            itemBinding.btnDelete.setOnClickListener {
                // Delete 버튼 클릭시 삭제한다.
                TodoData.API.delete(todo.id, {
                    TodoData.delete(todo)
                    TodoListFragment.instance.onResume()
                })
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListSectionViewHolder {
        return TodoListSectionViewHolder(
            FragmentTodoListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TodoListSectionViewHolder, position: Int) {
        // 날짜별 정렬
        section.todoList.sortWith(Comparator { v1, v2 ->
            val date1 = v1.date.split('-').map { it.toInt() }
            val date2 = v2.date.split('-').map { it.toInt() }
            if (date1[0] == date2[0]) {
                if (date1[1] == date2[1]) {
                    return@Comparator date1[2].compareTo(date2[2])
                }
                return@Comparator date1[1].compareTo(date2[1])
            }
            return@Comparator date1[0].compareTo(date2[0])
        })
        holder.bindItem(section.todoList[position], position)
    }

    override fun getItemCount(): Int {
        return section.todoList.size
    }


}