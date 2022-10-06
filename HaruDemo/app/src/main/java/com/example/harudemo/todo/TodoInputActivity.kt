package com.example.harudemo.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.widget.addTextChangedListener
import com.example.harudemo.databinding.ActivityTodoInputBinding
import com.google.android.material.datepicker.MaterialCalendar
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.LocalDate

class TodoInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoInputBinding
    private lateinit var startDatePickerFragment: DatePickerFragment
    private lateinit var endDatePickerFragment: DatePickerFragment
    private var dayButtons: ArrayList<ToggleButton> = ArrayList()

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

        binding.todoInput.addTextChangedListener {
            val text = binding.todoInput.text.toString()
            if (text.length == 1 && text != "#") {
                binding.todoInput.setText("#${binding.todoInput.text.toString()}")
                binding.todoInput.setSelection(2)
            }
        }

        binding.tvDurationStart.text = "${LocalDate.now()}"
        binding.tvDurationEnd.text = "${LocalDate.now()}"

        binding.tvDurationStart.setOnClickListener {
            startDatePickerFragment = DatePickerFragment(it as TextView)
            startDatePickerFragment.show(supportFragmentManager, "시작 날짜 선택")
        }

        binding.tvDurationEnd.setOnClickListener {
            endDatePickerFragment = DatePickerFragment(it as TextView)
            endDatePickerFragment.show(supportFragmentManager, "끝 날짜 선택")
        }

        binding.btnAddTodo.setOnClickListener {
            val text = binding.todoInput.text.toString()
            val splited = splitText(text) ?: return@setOnClickListener

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