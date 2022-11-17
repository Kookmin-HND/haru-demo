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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentTodoListItemBinding
import com.example.harudemo.fragments.TodoFragment
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.TodoData
import com.example.harudemo.todo.TodoInputActivity
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.TodoLog
import java.net.URI
import java.security.PrivateKey
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class TodoListSectionAdapter(private val index: Int, private val completed: Boolean) :
    RecyclerView.Adapter<TodoListSectionAdapter.TodoListSectionViewHolder>() {
    var section: Section? = null
        set(value) {
            if (section == null) {
                field = value
                this@TodoListSectionAdapter.notifyItemInserted(field?.todos?.size!!)
                return
            }
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                val older = field
                val newer = value

                override fun getOldListSize(): Int {
                    return older?.todos?.size!!
                }

                override fun getNewListSize(): Int {
                    return newer?.todos?.size!!
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return older?.todos!![oldItemPosition].id == newer?.todos!![newItemPosition].id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return older?.todos!![oldItemPosition].folder == newer?.todos!![newItemPosition].folder &&
                            older.todos[oldItemPosition].content == newer.todos[newItemPosition].content &&
                            older.logs[oldItemPosition].first() == newer.logs[newItemPosition].first()
                }
            })

            Timer("complete_todo", true).schedule(1000) {
                field = value
                TodoListFragment.instance.activity?.runOnUiThread {
                    result.dispatchUpdatesTo(this@TodoListSectionAdapter)
                    if (field?.logs?.any { it.isNotEmpty() } == false) {
                        TodoListFragment.instance.todoListAdapter.sections =
                            TodoListFragment.instance.todoListAdapter.sections.filter { it.title != section?.title }
                    }
                }
            }
        }

    inner class TodoListSectionViewHolder(private val itemBinding: FragmentTodoListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private val days = arrayListOf("월", "화", "수", "목", "금", "토", "일")

        init {
            itemBinding.btnCheckTodo.buttonTintList =
                ColorStateList.valueOf(Color.parseColor(TodoListFragment.COLORS[index % TodoListFragment.COLORS.size]))
        }

        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bindItem(todo: Todo, position: Int) {
            // Section으로부터 받은 Todo를 단순히 데이터 삽입
            itemBinding.btnCheckTodo.isChecked =
                section?.logs?.get(position)?.first()?.completed == true
            if (section?.logs?.get(position)?.first()?.completed == true) {
                itemBinding.tvTodoContent.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            itemBinding.tvTodoContent.text = todo.content

            val dateToken =
                section?.logs?.get(position)?.first()?.date!!.split('-').map { it.toInt() }
            var duration = " - 매주"
            for (i in 0 until todo.days.size) {
                if (todo.days[i]) {
                    duration += " ${days[i]},"
                }
            }
            if (duration == " - 매주") {
                duration = " "
            }
            val date = LocalDate.of(dateToken[0], dateToken[1], dateToken[2])
            itemBinding.tvTodoDate.text = "$date (${days[date.dayOfWeek.value - 1]}) ${
                duration.slice(0 until duration.length - 1)
            }"

            // Update 실행 Action
            itemBinding.root.setOnClickListener {
                if (section?.logs?.get(position)?.first()?.completed == true) {
                    return@setOnClickListener
                }

                val intent = Intent(it.context, TodoInputActivity::class.java)
                intent.putExtra("update", true)
                intent.putExtra("todo", todo)
                intent.putExtra("log", section?.logs?.get(position)?.first())
                it.context.startActivity(intent)
            }

            // completed Toggle Action
            itemBinding.btnCheckTodo.setOnClickListener {
                section?.logs!![position].first().let { log ->
                    TodoData.API.checkTodo(
                        todo.id,
                        log.date,
                        !log.completed,
                        {
                            section?.let { it1 ->
                                val newLogs =
                                    it1.logs.map { logs -> logs.filter { it2 -> it2 != log } } as ArrayList<ArrayList<TodoLog>>
                                var newTodos = it1.todos
                                if (newLogs[position].isEmpty()) {
                                    newTodos.removeAt(position)
                                }

                                section = Section(
                                    it1.title,
                                    newTodos,
                                    newLogs,
                                )
                            }
                        }
                    )
                }
            }

            itemBinding.btnDelete.setOnClickListener {
                // Delete 버튼 클릭시 삭제한다.
                TodoData.API.delete(todo.id, {
                    section?.let { it1 ->
                        val newTodos =
                            it1.todos.filter { it2 -> it2.id != todo.id } as ArrayList<Todo>
                        val newLogs =
                            it1.logs.filter { it2 -> it2.first().todoId != todo.id } as ArrayList<ArrayList<TodoLog>>

                        section = Section(
                            it1.title,
                            newTodos,
                            newLogs
                        )
                    }
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
        holder.bindItem(section?.todos!![position], position)
    }

    override fun getItemCount(): Int {
        return section?.todos?.size!!
    }
}