package com.example.harudemo.fragments.todo_fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.time.LocalDate

// 입력시에 기간으로 입력시 날짜를 선택하기 위한 만들어진 DatePicker 클래스
class DatePickerFragment(private val textView: TextView) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val now = LocalDate.now()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // LocalDate에서는 Calendar에서 받아온 month에다가 1을 더해줘야 같기때문에 더해준다. 이외는 같음.
        val date = LocalDate.of(year, month + 1, day)
        textView.text = date.toString()
    }
}