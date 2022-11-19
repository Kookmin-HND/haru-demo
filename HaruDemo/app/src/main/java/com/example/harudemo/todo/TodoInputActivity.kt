package com.example.harudemo.todo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import com.example.harudemo.databinding.ActivityTodoInputBinding
import com.example.harudemo.fragments.todo_fragments.DatePickerFragment
import com.example.harudemo.fragments.todo_fragments.TodoListFragment
import com.example.harudemo.todo.adapters.NewTodoSectionAdapter
import com.example.harudemo.todo.types.Todo
import com.example.harudemo.todo.types.TodoLog
import com.example.harudemo.todo.types.ViewMode
import com.example.harudemo.utils.CustomToast
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.collections.ArrayList

class TodoInputActivity : AppCompatActivity() {
    private var binding: ActivityTodoInputBinding? = null
    private var startDatePickerFragment: DatePickerFragment? = null // 기간으로 입력받을 때, 시작 날짜 선택하는 변수
    private var endDatePickerFragment: DatePickerFragment? = null // 기간으로 입력받을 때, 끝 날짜 선택하는 변수
    private var dayButtons: ArrayList<ToggleButton?> =
        arrayListOf(null) // 일 ~ 토 버튼을 리스트로 가지는 변수
    private var viewMode: Int = -1 // 현재 무슨 형식으로 데이터를 입력받고 있는지 확인하는 변수
    val days = arrayListOf(false, false, false, false, false, false, false)

    @SuppressLint("NotifyDataSetChanged", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoInputBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        dayButtons.add(binding?.btnSun!!)
        dayButtons.add(binding?.btnMon!!)
        dayButtons.add(binding?.btnTue!!)
        dayButtons.add(binding?.btnWed!!)
        dayButtons.add(binding?.btnThu!!)
        dayButtons.add(binding?.btnFri!!)
        dayButtons.add(binding?.btnSat!!)

        // Input창에 포커스가 들어가면 #을 삽입해줌.
        binding?.todoInput?.setOnFocusChangeListener { _, _ ->
            if (binding?.todoInput?.text?.length == 0) {
                binding?.todoInput?.setText("#")
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
        for (i in 1 until dayButtons.size) {
            dayButtons[i]?.setOnClickListener {
                goneCalendarView()
                days[i - 1] = !days[i - 1]
            }
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
        binding?.calendar?.setOnDateChangedListener { _, _, _ -> goneDurationView() }
        binding?.calendar?.setOnMonthChangedListener { _, _ -> goneDurationView() }

        // 업데이트하려고 들어온 상태인지 확인하려는 변수
        val updated = intent.getBooleanExtra("update", false)
        if (updated) {
            // TodoData를 미리 입력한다.
            val todoPair: Pair<Todo, List<TodoLog>> =
                intent.getSerializableExtra("todo") as Pair<Todo, List<TodoLog>>
            val todo = todoPair.first
            val log = todoPair.second.first()
            val text = "#${todo.folder} ${todo.content}"
            binding?.todoInput?.setText(text)
            val splittedDate = log.date.split('-').map { it.toInt() }
            val currentDate = SimpleDateFormat("yyyy-MM-dd").parse(
                LocalDate.of(splittedDate[0], splittedDate[1], splittedDate[2]).toString()
            )

            // 업데이트
            if (todo.days.any { it }) {
                // 기간으로 입력을 받았을 때
                viewMode = ViewMode.Duration
                binding?.cvCalendarView?.visibility = View.GONE

                val endDateLog = todoPair.second.last()
                val splittedEndDate = endDateLog.date.split('-').map { it.toInt() }
                val startDate = LocalDate.of(splittedDate[0], splittedDate[1], splittedDate[2])
                val endDate =
                    LocalDate.of(splittedEndDate[0], splittedEndDate[1], splittedEndDate[2])

                binding?.tvDurationStart?.text = startDate.toString()
                binding?.tvDurationEnd?.text = endDate.toString()
            } else {
                // 캘린더로 입력을 받았을 때
                // 기간 선택 방식은 제외
                viewMode = ViewMode.Calendar
                binding?.cvDurationView?.visibility = View.GONE
            }

            binding?.calendar?.setCurrentDate(currentDate)
            binding?.calendar?.setSelectedDate(currentDate)
        } else {
            // 데이터 생성
            // 기본적으로 시작 날짜를 오늘로 설정한다.
            binding?.tvDurationStart?.text = "${LocalDate.now()}"
            binding?.tvDurationEnd?.text = "${LocalDate.now()}"
        }

        // 모든 입력이 완료되면 추가 버튼을 클릭했을 때 발생하는 이벤트
        binding?.btnAddTodo?.setOnClickListener {
            if (!validation(binding?.todoInput?.text.toString())) {
                CustomToast.makeText(this, "\"#폴더 내용\"의 형식으로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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

                for (i in 0 until days.size) {
                    days[i] = false
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
                    CustomToast.makeText(this, "시작 날짜가 더 앞설 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 시작 날짜가 끝 날짜를 넘어설 때까지 모든 날짜와 선택한 요일을 1:1로 확인하여 저장.
                while (!startDate.isAfter(endDate)) {
                    Log.d("[debug]", startDate.toString())
                    var day = startDate.dayOfWeek.value + 1
                    if (day == 8) day = 1
                    Log.d(
                        "[debug]",
                        "$day ${dayButtons[day]?.isChecked}, ${dayButtons[day]?.text}"
                    )
                    if (dayButtons[day]?.isChecked == true) {
                        datesList.add(startDate.toString())
                    }
                    startDate = startDate.plusDays(1)
                }
                Log.d("[debug]", days.toString())
            }

            if (folder.isBlank() || content.isBlank()) {
                CustomToast.makeText(this, "폴더, 내용 중 입력되지 않은 것이 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (datesList.isEmpty()) {
                CustomToast.makeText(this, "날짜를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (updated) {
                // DB UPDATE
                val todoPair: Pair<Todo, List<TodoLog>> =
                    intent.getSerializableExtra("todo") as Pair<Todo, List<TodoLog>>
                val todo = todoPair.first
                val log = todoPair.second.first()
                TodoData.API.update(todo.id, folder, content, datesList, days)
            } else {
                // DB에 데이터 추가
                TodoData.API.create("cjeongmin27@gmail.com", folder, content, datesList, days)
            }

            for(data in datesList) {
                var splitdata = data.split("-")

//                val calendar: Calendar = Calendar.getInstance().apply { // 1
//                    timeInMillis = System.currentTimeMillis()
//                    set(Calendar.YEAR, splitdata[0].toInt())
//                    set(Calendar.MONTH, splitdata[1].toInt()-1)
//                    set(Calendar.DAY_OF_YEAR, splitdata[2].toInt())
//                    set(Calendar.AM_PM, Calendar.AM)
//                    set(Calendar.HOUR_OF_DAY, 9)
//                    set(Calendar.MINUTE, 0)
//                }
//
//                MainActivity.getInstance()?.addAlarm(calendar)
            }

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
            button?.visibility = View.GONE
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
            button?.visibility = View.VISIBLE
        }
    }

    // 입력에 문제가 있는지 판별하는 함수
    private fun validation(text: String): Boolean {
        val regex = Regex("#[a-zA-Z0-9ㄱ-ㅎ가-힣]+\\s+[\\sa-zA-Z0-9ㄱ-ㅎ가-힣~!]+")
        return regex.matches(text)
    }

    // TextEdit으로부터 입력을 받은 text를 폴더와 Content 내용으로 분리한다.
    // 만약, Content가 없는 경우엔 null을 반환한다.
    private fun splitText(text: String): ArrayList<String>? {
        val trimText = text.trim()
        val res: ArrayList<String> = ArrayList()
        val splitted = trimText.split(" ")
        if (splitted.size < 2) {
            return null
        }
        res.add(splitted[0].slice(1 until splitted[0].length)) // #폴더
        res.add(splitted.slice(1 until splitted.size).joinToString(" ")) // content
        return res
    }
}