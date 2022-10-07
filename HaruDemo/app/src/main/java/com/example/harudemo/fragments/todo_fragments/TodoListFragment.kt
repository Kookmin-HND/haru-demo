package com.example.harudemo.fragments.todo_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.harudemo.TodoDummyData
import com.example.harudemo.databinding.FragmentTodoListBinding
import com.example.harudemo.todo.types.Section
import com.example.harudemo.todo.adapters.TodoListAdapter

class TodoListFragment: Fragment() {
    companion object {
        const val TAG: String = "[TODO_LIST-LOG]"

        fun newInstance(): TodoListFragment {
            return TodoListFragment()
        }
    }

    private var binding: FragmentTodoListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)

        // TodoFragment로부터 전달된 값에 따라 TodoList Fragment에서 표시할 정보를 sections 배열에 저장 후
        // Recycler View에 전달
        var sections = ArrayList<Section>()
        val by = arguments?.getString("by") as String
        Log.d(TAG, by)
        when (by) {
            "today" -> {
                sections = TodoDummyData.getTodayTodoByFolder()
            }
            "week" -> {
                sections = TodoDummyData.getWeekTodoByDate()
            }
            "all" -> {
                sections = TodoDummyData.getAllSectionsByFolder()
            }
            "completed" -> {
                sections = TodoDummyData.getAllCompletedTodoByDate()
            }
            "folder" -> {
                val folderTitle = arguments?.getString("folder-title") as String
                sections = TodoDummyData.getFolderByFolderTitle(folderTitle)
            }
            else -> {

            }
        }

        val todoListAdapter = TodoListAdapter(sections)
        binding?.rvTodoSectionList?.adapter = todoListAdapter
        binding?.rvTodoSectionList?.layoutManager = LinearLayoutManager(
            binding?.root?.context,
            LinearLayoutManager.VERTICAL,
            false
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "하루"
    }
}