package com.example.harudemo.todo.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.databinding.FragmentTodoListItemBinding
import com.example.harudemo.fragments.TodoFragment
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.TodoData
import com.example.harudemo.todo.TodoInputActivity
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.types.Todo
import java.time.LocalDate

class TodoListSectionAdapter(private val section: Section) :
    RecyclerView.Adapter<TodoListSectionAdapter.TodoListSectionViewHolder>() {
    inner class TodoListSectionViewHolder(private val itemBinding: FragmentTodoListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindItem(todoItem: Todo) {
            // Section으로부터 받은 Todo를 단순히 데이터 삽입
            val days = arrayListOf<String>("월", "화", "수", "목", "금", "토", "일")

            itemBinding.tvTodoContent.text = todoItem.content

            val dateToken = todoItem.date.split('-').map { it.toInt() }
            val date = LocalDate.of(dateToken[0], dateToken[1], dateToken[2])
            itemBinding.tvTodoDate.text = "$date (${days[date.dayOfWeek.value - 1]})"

            itemBinding.btnDelete.setOnClickListener {
                // Delete 버튼 클릭시, 삭제하고 todos 배열에서도 삭제한다. 만약, 폴더가 비어있게 되면 이도 삭제한다.
                TodoData.deleteTodo(todoItem.id, {
                    TodoData.todos.removeIf { it.id == todoItem.id }
                    TodoData.todosByFolder[todoItem.folder]?.removeIf { it.id == todoItem.id }
                    if (TodoData.todosByFolder[todoItem.folder]?.isEmpty() == true) {
                        TodoData.todosByFolder.remove(todoItem.folder)
                        TodoFragment.folderListAdapter.notifyItemRemoved(TodoData.todosByFolder.keys.size)
                    }

                    section.todoList.removeIf { it.id == todoItem.id }
                    this@TodoListSectionAdapter.notifyDataSetChanged()
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
        // 새로 받은 TodoList내에서 같은 id끼리는 정렬되어 있지만 다른 id에서는 정렬되지 않아 보이는데 혼선이 있어
        // 새롭게 다시 정렬.
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
        holder.bindItem(section.todoList[position])
    }

    override fun getItemCount(): Int {
        return section.todoList.size
    }

}