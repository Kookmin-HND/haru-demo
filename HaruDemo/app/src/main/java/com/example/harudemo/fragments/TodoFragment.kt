package com.example.harudemo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentTodoBinding
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.TodoInputActivity
import com.example.harudemo.todo.adapters.TodoFolderListAdapter

/**
 * TODO: 사용자의 최대 Todo 개수 제한 필요
 * FIXME: 업데이트 상태에서 기간 입력시 날짜 선택 안해도 기존 선택으로 가도록 수정
 **/

class TodoFragment : Fragment() {
    companion object {
        private var _instance: TodoFragment? = null
        val instance: TodoFragment
            get() {
                if (_instance == null) {
                    _instance = TodoFragment()
                }
                return _instance!!
            }

        private var _folderListAdapter: TodoFolderListAdapter? = null
        val folderListAdapter: TodoFolderListAdapter
            get() {
                if (_folderListAdapter == null) {
                    _folderListAdapter = TodoFolderListAdapter(instance.requireActivity())
                }
                return _folderListAdapter!!
            }
    }

    private var binding: FragmentTodoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoBinding.inflate(inflater, container, false)
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

        // 데이터 추가 버튼 클릭시에 새로운 액티비티로 이동
        binding?.btnAddTodo?.setOnClickListener {
            val intent = Intent(context, TodoInputActivity::class.java)
            startActivity(intent)
        }

        // 스크롤시 버튼 숨기기 토글
        binding?.rvFolderList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                    binding?.btnAddTodo?.hide();
                else if (dy < 0)
                    binding?.btnAddTodo?.show();
            }
        })

        binding?.rvFolderList?.adapter = folderListAdapter
        binding?.rvFolderList?.layoutManager = LinearLayoutManager(
            binding?.root?.context,
            LinearLayoutManager.VERTICAL,
            false,
        )
    }

    override fun onResume() {
        super.onResume()
        // Folder Item을 Recycler View에 추가
        folderListAdapter.fetchData()
    }

    // 이 함수는 클릭된 버튼에 따라 Fragment에서 어떤 정보를 표시할지 정할 수 있도록
    // 데이터를 Bundle에 추가해서 넣고 TodoList Fragment로 연결한다.
    private fun onBtnClicked(view: View) {
        val bundle = Bundle()
        TodoListFragment.instance.arguments = bundle

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
            ?.replace(R.id.fragments_frame, TodoListFragment.instance)?.commit()
    }
}