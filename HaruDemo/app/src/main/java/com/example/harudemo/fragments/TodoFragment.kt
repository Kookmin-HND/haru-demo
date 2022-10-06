package com.example.harudemo.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.harudemo.R
import com.example.harudemo.TodoDummyData
import com.example.harudemo.databinding.FragmentTodoBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.TodoInputActivity
import com.example.harudemo.todo.adapters.TodoFolderListAdapter

class TodoFragment : Fragment() {
    companion object {
        const val TAG: String = "[TODO-LOG]"

        fun newInstance(): TodoFragment {
            return TodoFragment()
        }
    }

    private lateinit var todoListFragment: TodoListFragment
    private lateinit var binding: FragmentTodoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoBinding.inflate(inflater, container, false)

        activity?.let {
            val folderListAdapter = TodoFolderListAdapter(TodoDummyData.getFolderTitles(), it)
            binding.rvFolderList.adapter = folderListAdapter
            binding.rvFolderList.layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.VERTICAL,
                false,
            )
        }

        binding.btnAddTodo.setOnClickListener {
            val intent = Intent(context, TodoInputActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "하루"
        binding.btnCompleted.setOnClickListener { onBtnClicked(it) }
        binding.btnToday.setOnClickListener { onBtnClicked(it) }
        binding.btnWeek.setOnClickListener { onBtnClicked(it) }
        binding.btnAll.setOnClickListener { onBtnClicked(it) }
    }

    private fun onBtnClicked(view: View) {
        val bundle = Bundle()
        todoListFragment = TodoListFragment.newInstance()
        todoListFragment.arguments = bundle

        when ((view as Button).text.toString()) {
            "오늘" -> {
                bundle.putString("by", "today")
            }
            "일주일" -> {
                bundle.putString("by", "week")
            }
            "전체" -> {
                bundle.putString("by", "all")
            }
            "완료된 항목" -> {
                bundle.putString("by", "completed")
            }
            else -> {
            }
        }

        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragments_frame, todoListFragment)?.commit()
    }

}