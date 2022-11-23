package com.example.harudemo.fragments.todo_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harudemo.R
import com.example.harudemo.databinding.FragmentTodoListBinding
import com.example.harudemo.fragments.TodoFragment
import com.example.harudemo.todo.TodoData
import com.example.harudemo.todo.adapters.NewTodoListAdapter
import com.example.harudemo.todo.types.Section
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.User
import java.time.LocalDate
import kotlin.collections.ArrayList

class TodoListFragment : Fragment() {
    // UI Update를 위해 Instance 접근 가능하게끔하고,
    // 이를 Singleton 방식으로하여 객체를 하나로하여 재활용한다.
    companion object {
        private var _instance: TodoListFragment? = null
        val instance: TodoListFragment
            get() {
                if (_instance == null) {
                    _instance = TodoListFragment()
                }
                return _instance!!
            }

        val COLORS = listOf(
            "#EE4266",
            "#FC9E4F",
            "#ECF39E",
            "#114B5F",
            "#EDD382",
            "#90A955",
            "#E2E4F6",
            "#FF521B",
            "#1F271B",
            "#1A936F",
            "#DEFFFC",
        )
    }

    var binding: FragmentTodoListBinding? = null
    private var callback: OnBackPressedCallback? = null
    private var todoFragment: TodoFragment = TodoFragment.instance
    var todoListAdapter: NewTodoListAdapter = NewTodoListAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 뒤로가기 키 핸들링위한 함수, 뒤로가기 눌리면 todoFragment로 돌아간다.
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                todoFragment = TodoFragment.instance
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragments_frame, todoFragment)?.commit()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "하루"
        todoListAdapter = NewTodoListAdapter()
        binding?.rvTodoSectionList?.apply {
            this.itemAnimator = null
            adapter = todoListAdapter
            layoutManager = LinearLayoutManager(
                binding?.root?.context, LinearLayoutManager.VERTICAL, false
            )
        }
    }

    override fun onResume() {
        super.onResume()
        updateSections()
    }

    override fun onDetach() {
        super.onDetach()
        // 뒤로가기 키 이벤트 삭제
        callback?.remove()
    }

    fun updateSections() {
        when (arguments?.getString("by") as String) {
            "today" -> {
                val today = LocalDate.now().toString()
                TodoData.API.getTodosByDate(
                    User.info?.email!!,
                    today,
                    false,
                    {
                        if (it.first.isEmpty()) return@getTodosByDate
                        todoListAdapter.isDateSection = true
                        todoListAdapter.submitList(listOf(
                            Section(
                                "하루",
                                it.first,
                                it.second.map { log -> listOf(log) }
                            )
                        ))
                    },
                    {
                        CustomToast.makeText(
                            requireContext(),
                            "오늘의 목록을 불러오는데 실패하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
            }
            "week" -> {
                val dates = ArrayList<String>()
                var today = LocalDate.now()
                for (i in 1..7) {
                    dates.add(today.toString())
                    today = today.plusDays(1)
                }
                TodoData.API.getTodosByDateInDates(User.info?.email!!, dates, false, { it ->
                    val result: ArrayList<Section> = arrayListOf()
                    for (section in it) {
                        if (section.value.first.isEmpty()) continue
                        result.add(
                            Section(
                                section.key,
                                section.value.first,
                                section.value.second.map { log -> listOf(log) }
                            )
                        )
                    }
                    result.sortWith { v1, v2 ->
                        val date1 = v1.title.split("-").map { it.toInt() }
                        val date2 = v2.title.split("-").map { it.toInt() }
                        if (date1[0] == date2[0]) {
                            if (date1[1] == date2[1]) {
                                return@sortWith date1[2].compareTo(date2[2])
                            }
                            return@sortWith date1[1].compareTo(date2[1])
                        }
                        return@sortWith date1[0].compareTo(date2[0])
                    }
                    todoListAdapter.isDateSection = true
                    todoListAdapter.submitList(result)
                }, {
                    CustomToast.makeText(
                        requireContext(),
                        "일주일 목록을 불러오는데 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                })

            }
            "all" -> {
                TodoData.API.getAllTodosByFolder(User.info?.email!!, false, {
                    val result: ArrayList<Section> = arrayListOf()
                    for (section in it) {
                        if (section.value.first.isEmpty()) continue
                        result.add(Section(section.key, section.value.first, section.value.second))
                    }
                    todoListAdapter.isDateSection = false
                    todoListAdapter.submitList(result)
                }, {
                    CustomToast.makeText(
                        requireContext(),
                        "모든 목록을 불러오는데 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                })
            }
            "completed" -> {
                TodoData.API.getAllTodosByDate(User.info?.email!!, true, {
                    val result: ArrayList<Section> = arrayListOf()
                    for (section in it) {
                        if (section.value.first.isEmpty()) continue
                        result.add(
                            Section(
                                if (section.key == LocalDate.now()
                                        .toString()
                                ) "오늘" else section.key,
                                section.value.first,
                                section.value.second.map { log -> listOf(log) })
                        )
                    }
                    result.sortWith { v1, v2 ->
                        if (v1.title == "오늘") {
                            return@sortWith -1
                        } else if (v2.title == "오늘") {
                            return@sortWith 1
                        }
                        val date1 = v1.title.split("-").map { it.toInt() }
                        val date2 = v2.title.split("-").map { it.toInt() }
                        if (date1[0] == date2[0]) {
                            if (date1[1] == date2[1]) {
                                return@sortWith date1[2].compareTo(date2[2])
                            }
                            return@sortWith date1[1].compareTo(date2[1])
                        }
                        return@sortWith date1[0].compareTo(date2[0])
                    }
                    todoListAdapter.isDateSection = true
                    todoListAdapter.submitList(result)
                }, {
                    CustomToast.makeText(
                        requireContext(),
                        "완료 목록을 불러오는데 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                })
            }
            "folder" -> {
                val folderTitle = arguments?.getString("folder-title") as String
                TodoData.API.getTodosByFolder(User.info?.email!!, folderTitle, false, {
                    if (it.first.isEmpty()) return@getTodosByFolder
                    todoListAdapter.isDateSection = false
                    todoListAdapter.submitList(listOf(Section(folderTitle, it.first, it.second)))
                }, {
                    CustomToast.makeText(
                        requireContext(),
                        "#${folderTitle}를 불러오는데 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }, {})
            }
            else -> {}
        }
    }

    fun decideView(sections: List<Section>?) {
        if (sections?.isEmpty() == true) {
            binding?.rvTodoSectionList?.visibility = View.GONE
            binding?.tvEmpty?.visibility = View.VISIBLE
        } else {
            binding?.rvTodoSectionList?.visibility = View.VISIBLE
            binding?.tvEmpty?.visibility = View.GONE
        }
    }

}