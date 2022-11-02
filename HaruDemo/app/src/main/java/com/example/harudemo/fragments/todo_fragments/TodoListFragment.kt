package com.example.harudemo.fragments.todo_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentTodoListBinding
import com.example.harudemo.fragments.TodoFragment
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
    private var callback: OnBackPressedCallback? = null
    private var todoFragment: TodoFragment? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 뒤로가기 키 핸들링위한 함수, 뒤로가기 눌리면 todoFragment로 돌아간다.
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                todoFragment = TodoFragment.getInstance()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragments_frame, todoFragment!!)?.commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback!!)
    }

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
//                sections = TodoDummyData.getTodayTodoByFolder()
            }
            "week" -> {
//                sections = TodoDummyData.getWeekTodoByDate()
            }
            "all" -> {
//                sections = TodoDummyData.getAllSectionsByFolder()
            }
            "completed" -> {
//                sections = TodoDummyData.getAllCompletedTodoByDate()
            }
            "folder" -> {
                val folderTitle = arguments?.getString("folder-title") as String
//                sections = TodoDummyData.getFolderByFolderTitle(folderTitle)
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

    override fun onDetach() {
        super.onDetach()
        // 뒤로가기 키 이벤트 삭제
        callback?.remove()
    }
}