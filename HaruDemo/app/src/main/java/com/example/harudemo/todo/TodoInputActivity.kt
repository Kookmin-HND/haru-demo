package com.example.harudemo.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.widget.addTextChangedListener
import com.example.harudemo.R
import com.example.harudemo.TodoDummyData
import com.example.harudemo.databinding.ActivityTodoInputBinding
import com.example.harudemo.fragments.TodoFragment
import com.example.harudemo.todo.types.ViewMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.LocalDate

class TodoInputActivity : AppCompatActivity() {
    private var binding: ActivityTodoInputBinding? = null
    private var startDatePickerFragment: DatePickerFragment? = null
    private var endDatePickerFragment: DatePickerFragment? = null
    private var dayButtons: ArrayList<ToggleButton> = ArrayList()
    private var viewMode: Int = -1

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

        binding?.tvDurationStart?.text = "${LocalDate.now()}"
        binding?.tvDurationEnd?.text = "${LocalDate.now()}"

        binding?.todoInput?.addTextChangedListener {
            val text = binding?.todoInput?.text?.toString()
            if (text?.length == 1 && text != "#") {
                binding?.todoInput?.setText("#${binding?.todoInput?.text.toString()}")
                binding?.todoInput?.setSelection(2)
            }
        }

        binding?.cvDurationView?.setOnClickListener {
            goneCalendarView()
        }

        binding?.cvCalendarView?.setOnClickListener {
            goneDurationView()
        }

        for (button in dayButtons) {
            button.setOnClickListener { goneCalendarView() }
        }

        binding?.tvDurationStart?.setOnClickListener {
            goneCalendarView()
            startDatePickerFragment = DatePickerFragment(it as TextView)
            startDatePickerFragment?.show(supportFragmentManager, "시작 날짜 선택")
        }

        binding?.tvDurationEnd?.setOnClickListener {
            goneCalendarView()
            endDatePickerFragment = DatePickerFragment(it as TextView)
            endDatePickerFragment?.show(supportFragmentManager, "끝 날짜 선택")
        }

        binding?.calendar?.setOnDateChangedListener { widget, date, selected -> goneDurationView() }
        binding?.calendar?.setOnMonthChangedListener { widget, date -> goneDurationView() }

        binding?.btnAddTodo?.setOnClickListener {
            val text = binding?.todoInput?.text.toString()
            val splitted = splitText(text) ?: return@setOnClickListener

            if (viewMode == ViewMode.Calendar) {
                val dates = binding?.calendar?.selectedDates
                val datesList = arrayListOf<String>()
                for (date in dates!!) {
                    datesList.add("${date.year}-${date.month + 1}-${date.day}")
                }
                TodoDummyData.addTodo(splitted[0], splitted[1], datesList)
            } else {
                val start = binding?.tvDurationStart?.text.toString()
                val end = binding?.tvDurationEnd?.text.toString()

                val splittedStart = start.split('-').map { it.toInt() }
                val splittedEnd = end.split('-').map { it.toInt() }

                var startDate = LocalDate.of(splittedStart[0], splittedStart[1], splittedStart[2])
                val endDate = LocalDate.of(splittedEnd[0], splittedEnd[1], splittedEnd[2])

                if (endDate.isBefore(startDate)) {
                    Toast.makeText(this, "현재 날짜가 더 앞설 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val datesList = ArrayList<String>();
                while (!startDate.isAfter(endDate)) {
                    val day = startDate.dayOfWeek.value - 1
                    if (dayButtons[day].isChecked) {
                        datesList.add(startDate.toString())
                    }
                    startDate = startDate.plusDays(1)
                }
                TodoDummyData.addTodo(
                    splitted[0],
                    splitted[1],
                    datesList
                )
            }
            this.finish()
        }
    }

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