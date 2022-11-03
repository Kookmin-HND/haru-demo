package com.example.harudemo.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.widget.addTextChangedListener
import com.example.harudemo.MainActivity
import com.example.harudemo.R
import com.example.harudemo.databinding.ActivityTodoInputBinding
import com.example.harudemo.databinding.FragmentTodoBinding
import com.example.harudemo.fragments.TodoFragment
import com.example.harudemo.fragments.todo_fragments.DatePickerFragment
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.ViewMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.synthetic.main.activity_sns_add_post.*
import java.time.LocalDate

class TodoInputActivity : AppCompatActivity() {
    private var binding: ActivityTodoInputBinding? = null
    private var startDatePickerFragment: DatePickerFragment? = null // 기간으로 입력받을 때, 시작 날짜 선택하는 변수
    private var endDatePickerFragment: DatePickerFragment? = null // 기간으로 입력받을 때, 끝 날짜 선택하는 변수
    private var dayButtons: ArrayList<ToggleButton> = ArrayList() // 일 ~ 토 버튼을 리스트로 가지는 변수
    private var viewMode: Int = -1 // 현재 무슨 형식으로 데이터를 입력받고 있는지 확인하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoInputBinding.inflate(layoutInflater)
        binding?.calendar?.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
        setContentView(binding?.root)

        dayButtons.add(binding?.btnSun!!)
        dayButtons.add(binding?.btnMon!!)
        dayButtons.add(binding?.btnTue!!)
        dayButtons.add(binding?.btnWed!!)
        dayButtons.add(binding?.btnThu!!)
        dayButtons.add(binding?.btnFri!!)
        dayButtons.add(binding?.btnSat!!)

        // 기본적으로 시작 날짜를 오늘로 설정한다.
        binding?.tvDurationStart?.text = "${LocalDate.now()}"
        binding?.tvDurationEnd?.text = "${LocalDate.now()}"

        // Todo 입력시에 #이 없으면 #을 붙여주고 커서 위치를 옮김
        binding?.todoInput?.addTextChangedListener {
            val text = binding?.todoInput?.text?.toString()
            if (text?.length == 1 && text != "#") {
                binding?.todoInput?.setText("#${binding?.todoInput?.text.toString()}")
                binding?.todoInput?.setSelection(2)
            }
        }

        // 하나의 방식으로만 입력받게 하기 위해서 강제적으로 표시 전환
        binding?.cvDurationView?.setOnClickListener {
            goneCalendarView()
        }

        // 하나의 방식으로만 입력받게 하기 위해서 강제적으로 표시 전환
        binding?.cvCalendarView?.setOnClickListener {
            goneDurationView()
        }

        // 하나의 방식으로만 입력받게 하기 위해서 강제적으로 표시 전환
        for (button in dayButtons) {
            button.setOnClickListener { goneCalendarView() }
        }

        // DurationStart 텍스트뷰 클릭시 DatePicker 표시
        binding?.tvDurationStart?.setOnClickListener {
            goneCalendarView()
            startDatePickerFragment = DatePickerFragment(it as TextView)
            startDatePickerFragment?.show(supportFragmentManager, "시작 날짜 선택")
        }

        // DurationStart 텍스트뷰 클릭시 DatePicker 표시
        binding?.tvDurationEnd?.setOnClickListener {
            goneCalendarView()
            endDatePickerFragment = DatePickerFragment(it as TextView)
            endDatePickerFragment?.show(supportFragmentManager, "끝 날짜 선택")
        }

        // 하나의 방식으로만 입력받게 하기 위해서 강제적으로 표시 전환
        binding?.calendar?.setOnDateChangedListener { widget, date, selected -> goneDurationView() }
        binding?.calendar?.setOnMonthChangedListener { widget, date -> goneDurationView() }

        // 모든 입력이 완료되면 추가 버튼을 클릭했을 때 발생하는 이벤트
        binding?.btnAddTodo?.setOnClickListener {
            // 텍스트 인풋으로부터 받은 텍스트
            val text = binding?.todoInput?.text.toString()
            // 만약, 그 텍스트를 토큰화를 했을때 배열 사이즈가 2보다 작으면 종료.
            val splitted = splitText(text) ?: return@setOnClickListener
            val folder = splitted[0]
            val content = splitted[1]
            val datesList = arrayListOf<String>()

            if (viewMode == ViewMode.Calendar) {
                // 현재 입력 방식이 캘린더인 경우.
                val dates = binding?.calendar?.selectedDates
                // 모든 날짜를 datesList에 추가한다.
                for (date in dates!!) {
                    datesList.add("${date.year}-${date.month + 1}-${date.day}")
                }
            } else {
                // 현재 입력 방식이 기간인 경우.
                // 시작 날짜와 끝 날짜를 가져와서 문자열로 변환한다.
                val start = binding?.tvDurationStart?.text.toString()
                val end = binding?.tvDurationEnd?.text.toString()

                // 위에서 가져온 데이터를 년/월/일로 나누면서 숫자로 변환하여 List로 가진다.
                val splittedStart = start.split('-').map { it.toInt() }
                val splittedEnd = end.split('-').map { it.toInt() }

                // 위에서 가져온 List를 LocalDate 객체로 변환.
                var startDate = LocalDate.of(splittedStart[0], splittedStart[1], splittedStart[2])
                val endDate = LocalDate.of(splittedEnd[0], splittedEnd[1], splittedEnd[2])

                // 만약, 끝 날짜가 시작 날짜보다 앞에 있을 경우 예외처리.
                if (endDate.isBefore(startDate)) {
                    Toast.makeText(this, "시작 날짜가 더 앞설 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 시작 날짜가 끝 날짜를 넘어설 때까지 모든 날짜와 선택한 요일을 1:1로 확인하여 저장.
                while (!startDate.isAfter(endDate)) {
                    val day = startDate.dayOfWeek.value - 1
                    if (dayButtons[day].isChecked) {
                        datesList.add(startDate.toString())
                    }
                    startDate = startDate.plusDays(1)
                }

            }

            // DB에 Todo 추가
            TodoData.addTodo(
                "cjeongmin27@gmail.com",
                folder,
                content,
                datesList,
                {
                    // DB에는 추가되었고,
                    // 전역적으로 관리하는 데이터에도 추가해준다.
                    TodoData.todos.addAll(it)
                    for (todo in it) {
                        TodoData.folderNames.add(todo.folder)
                    }

                    // Recycler View를 새로고침한다.
                    TodoFragment.folderListAdapter.notifyItemInserted(TodoData.folderNames.size)
                }
            )
            // 입력이 정상적으로 되었다고 판단. Activity 종료
            finish()
        }
    }

    // 이 함수는 입력 방식을 제한하는데, CalendarView가 클릭시 DurationView를 감춘다.
    private fun goneDurationView() {
        if (viewMode == ViewMode.Calendar) {
            return
        }
        viewMode = ViewMode.Calendar
        binding?.clDuration?.visibility = View.GONE
        for (button in dayButtons) {
            button.visibility = View.GONE
        }
        binding?.flCalendarView?.visibility = View.VISIBLE
    }

    // 이 함수는 입력 방식을 제한하는데, DurationView가 클릭시 CalendarView를 감춘다.
    private fun goneCalendarView() {
        if (viewMode == ViewMode.Duration) {
            return
        }
        viewMode = ViewMode.Duration
        binding?.flCalendarView?.visibility = View.GONE
        binding?.clDuration?.visibility = View.VISIBLE
        for (button in dayButtons) {
            button.visibility = View.VISIBLE
        }
    }

    // TextEdit으로부터 입력을 받은 text를 폴더와 Todo 내용으로 분리한다.
    // 만약, Todo 내용이 없는 경우엔 null을 반환한다.
    private fun splitText(text: String): ArrayList<String>? {
        val trimText = text.trim()
        val res: ArrayList<String> = ArrayList()
        val splitted = trimText.split(" ")
        if (splitted.size < 2) {
            return null
        }
        res.add(splitted[0].slice(1 until splitted[0].length)) // #폴더
        res.add(splitted.slice(1 until splitted.size).joinToString(" "))
        return res
    }

}