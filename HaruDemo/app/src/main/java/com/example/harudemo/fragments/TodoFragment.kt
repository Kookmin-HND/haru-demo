package com.example.harudemo.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.harudemo.MainActivity
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentTodoBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.retrofit.RetrofitManager
import com.example.harudemo.todo.TodoData
import com.example.harudemo.todo.TodoInputActivity
import com.example.harudemo.todo.adapters.TodoFolderListAdapter
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.utils.Constants
import com.example.harudemo.utils.RESPONSE_STATUS

class TodoFragment : Fragment() {
    companion object {
        const val TAG: String = "[TODO-LOG]"
        private var instance: TodoFragment? = null
        private var folderListAdapter: TodoFolderListAdapter? = null

        // TodoFragment를 Singleton 방식으로 접근
        fun getInstance(): TodoFragment {
            if (instance == null) {
                instance = TodoFragment()
            }
            return instance!!
        }

        fun getAdapter(): TodoFolderListAdapter {
            if (folderListAdapter == null) {
                folderListAdapter = getInstance()?.activity?.let { TodoFolderListAdapter(it) }
            }
            return folderListAdapter!!
        }
    }

    private var todoListFragment: TodoListFragment? = null
    private var binding: FragmentTodoBinding? = null

    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoBinding.inflate(inflater, container, false)

        // DB에서 Todo Data를 불러온다
        if (TodoData.todos.isEmpty()) {
            TodoData.fetchTodos("cjeongmin27@gmail.com", {
                // 데이터를 불러오는데 성공하였을 때
                if (TodoData.todos.isEmpty()) {
                    TodoData.todos.addAll(it)
                    for (todo in it) {
                        TodoData.folderNames.add(todo.folder)
                    }
                }
                getAdapter().notifyItemInserted(TodoData.folderNames.size)
            }, {
                // 데이터를 불러오는데 실패하였을 때
                Toast.makeText(
                    this.context, "todo 목록을 불러오는데 실패하였습니다.", Toast.LENGTH_SHORT
                ).show()
            })
        }

        // Folder Item을 Recycler View에 추가
        binding?.rvFolderList?.adapter = getAdapter()
        binding?.rvFolderList?.layoutManager = LinearLayoutManager(
            binding?.root?.context,
            LinearLayoutManager.VERTICAL,
            false,
        )

        // todo 추가 버튼 클릭시에 새로운 액티비티로 이동
        binding?.btnAddTodo?.setOnClickListener {
            val intent = Intent(context, TodoInputActivity::class.java)
            startActivity(intent)
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "하루"
        // 여러 정렬 방법 버튼들 이벤트 리스너 설정 해당 함수는 아래에 있음
        binding?.btnCompleted?.setOnClickListener { onBtnClicked(it) }
        binding?.btnToday?.setOnClickListener { onBtnClicked(it) }
        binding?.btnWeek?.setOnClickListener { onBtnClicked(it) }
        binding?.btnAll?.setOnClickListener { onBtnClicked(it) }
    }

    // 이 함수는 클릭된 버튼에 따라 Fragment에서 어떤 정보를 표시할지 정할 수 있도록
    // 데이터를 Bundle에 추가해서 넣고 TodoList Fragment로 연결한다.
    private fun onBtnClicked(view: View) {
        val bundle = Bundle()
        todoListFragment = TodoListFragment.newInstance()
        todoListFragment?.arguments = bundle

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

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragments_frame, todoListFragment!!)?.commit()
    }

}