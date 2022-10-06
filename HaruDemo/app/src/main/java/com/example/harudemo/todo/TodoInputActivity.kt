package com.example.harudemo.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.widget.addTextChangedListener
import com.example.harudemo.TodoDummyData
import com.example.harudemo.databinding.ActivityTodoInputBinding
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.TodoDateInterface
import com.example.harudemo.todo.types.ViewMode
import com.google.android.material.datepicker.MaterialCalendar
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.synthetic.main.activity_todo_input.view.*
import java.time.LocalDate

class TodoInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoInputBinding
    private lateinit var startDatePickerFragment: DatePickerFragment
    private lateinit var endDatePickerFragment: DatePickerFragment
    private var dayButtons: ArrayList<ToggleButton> = ArrayList()
    private var viewMode: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTodoInputBinding.inflate(layoutInflater)
        binding.calendar.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
        setContentView(binding.root)

        dayButtons.add(binding.btnSun)
        dayButtons.add(binding.btnMon)
        dayButtons.add(binding.btnTue)
        dayButtons.add(binding.btnWed)
        dayButtons.add(binding.btnThu)
        dayButtons.add(binding.btnFri)
        dayButtons.add(binding.btnSat)

        binding.tvDurationStart.text = "${LocalDate.now()}"
        binding.tvDurationEnd.text = "${LocalDate.now()}"

        binding.todoInput.addTextChangedListener {
            val text = binding.todoInput.text.toString()
            if (text.length == 1 && text != "#") {
                binding.todoInput.setText("#${binding.todoInput.text.toString()}")
                binding.todoInput.setSelection(2)
            }
        }

        binding.cvDurationView.setOnClickListener {
            goneCalendarView()
        }

        binding.cvCalendarView.setOnClickListener {
            goneDurationView()
        }

        for (button in dayButtons) {
            button.setOnClickListener { goneCalendarView() }
        }

        binding.tvDurationStart.setOnClickListener {
            goneCalendarView()
            startDatePickerFragment = DatePickerFragment(it as TextView)
            startDatePickerFragment.show(supportFragmentManager, "시작 날짜 선택")
        }

        binding.tvDurationEnd.setOnClickListener {
            goneCalendarView()
            endDatePickerFragment = DatePickerFragment(it as TextView)
            endDatePickerFragment.show(supportFragmentManager, "끝 날짜 선택")
        }

        binding.calendar.setOnDateChangedListener { widget, date, selected -> goneDurationView() }
        binding.calendar.setOnMonthChangedListener { widget, date -> goneDurationView() }

        binding.btnAddTodo.setOnClickListener {
            val text = binding.todoInput.text.toString()
            val splitted = splitText(text) ?: return@setOnClickListener

            if (viewMode == ViewMode.Calendar) {
                val dates = binding.calendar.selectedDates
                for (date in dates) {
                    Log.d("[ADD]", "${date.year} : ${date.month} : ${date.day}")
                }
            } else {
                val start = binding.tvDurationStart.text.toString()
                val end = binding.tvDurationEnd.text.toString()

                val splittedStart = start.split('-').map { it.toInt() }
                val splittedEnd = end.split('-').map { it.toInt() }

                var startDate = LocalDate.of(splittedStart[0], splittedStart[1], splittedStart[2])
                val endDate = LocalDate.of(splittedEnd[0], splittedEnd[1], splittedEnd[2])

                if (endDate.isBefore(startDate)) {
                    Toast.makeText(this, "현재 날짜가 더 앞설 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                while (!startDate.isAfter(endDate)) {
                    val day = startDate.dayOfWeek.value - 1
                    if (dayButtons[day].isChecked) {
                        // Todo 추가하기
                    }
                    startDate = startDate.plusDays(1)
                }
            }
        }
    }

    private fun goneDurationView() {
        if (viewMode == ViewMode.Calendar) {
            return
        }
        viewMode = ViewMode.Calendar
        binding.clDuration.visibility = View.GONE
        for (button in dayButtons) {
            button.visibility = View.GONE
        }
        binding.flCalendarView.visibility = View.VISIBLE
    }

    private fun goneCalendarView() {
        if (viewMode == ViewMode.Duration) {
            return
        }
        viewMode = ViewMode.Duration
        binding.flCalendarView.visibility = View.GONE
        binding.clDuration.visibility = View.VISIBLE
        for (button in dayButtons) {
            button.visibility = View.VISIBLE
        }
    }


    private fun splitText(text: String): ArrayList<String>? {
        val trimText = text.trim()
        val res: ArrayList<String> = ArrayList()
        var i = 1
        var tmp = ""
        while (i < text.length) {
            if (trimText[i] == ' ') {
                res.add(tmp);
                tmp = ""
            } else {
                tmp += trimText[i]
            }
            i++
        }
        res.add(tmp)

        if (res.size == 0) {
            return null
        }
        return res
    }

}