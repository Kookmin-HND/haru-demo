package com.example.harudemo


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harudemo.databinding.ActivityMainBinding
import com.example.harudemo.fragments.*
import com.example.harudemo.retrofit.AuthRetrofitManager
import com.example.harudemo.service.Constant
import com.example.harudemo.service.MyReceiver
import com.example.harudemo.todo.TodoData
import com.example.harudemo.todo.types.Section
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.RESPONSE_STATUS
import com.example.harudemo.utils.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var snsFragment: SnsFragment? = null
    private var calendarFragment: CalendarFragment? = null
    private var todoFragment: TodoFragment? = null
    private var statisticsFragment: StatisticsFragment? = null
    private var etcFragment: EtcFragment? = null
    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null

    //todoinputactivty에서 메인엑티비티의 함수를 다루기 위한
    //instance 생성
    init{
        instance = this
    }

    companion object{
        private var instance:MainActivity? = null
        fun getInstance(): MainActivity? {
            return instance
        }
    }

    fun addAlarm(calendar: Calendar){
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, MyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager!!.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    //알람 취소 기능 나중에 필요할시 사용할 것
    fun cancelAlarm(){
        if(alarmManager != null && pendingIntent != null) alarmManager!!.cancel(pendingIntent)
    }


    //db 데이터 가져와서 maindata에 저장
    fun getData(refresh: Boolean = false){
        maindata.contents = Array(30){Array(13){Array(32){""} }}
        maindata.successrate = Array(30){Array(13){Array(32){0} }}

        TodoData.API.getAllTodosByFolder(User.info?.email!!, false, {

            val result: ArrayList<Section> = arrayListOf()
            for (section in it) {
                if (section.value.first.isEmpty()) continue
                result.add(Section(section.key, section.value.first, section.value.second))
            }

            for (i in result){
                for(w in i.todos){
                    for(j in i.logs){
                        for(k in j){
                            val date = k.date.split("-").map { it -> it.toString().toInt() }
                            val year = date[0]
                            val month = date[1]
                            val day = date[2]

                            if(k.todoId == w.id) {
                                maindata.contents[year - 2022][month - 1][day] += w.content+"\n"
                            }
                        }
                    }
                }
            }

        }, {
            CustomToast.makeText(
                this,
                "모든 목록을 불러오는데 실패하였습니다.",
                Toast.LENGTH_SHORT
            ).show()
        })

        TodoData.API.getAllTodosByFolder(User.info?.email!!, true, {

            val result: ArrayList<Section> = arrayListOf()
            for (section in it) {
                if (section.value.first.isEmpty()) continue
                result.add(Section(section.key, section.value.first, section.value.second))
            }

            for (i in result){
                for(w in i.todos){
                    for(j in i.logs){
                        for(k in j){
                            val date = k.date.split("-").map { it -> it.toString().toInt() }
                            val year = date[0]
                            val month = date[1]
                            val day = date[2]

                            if(k.todoId == w.id) {
                                maindata.contents[year - 2022][month - 1][day] += w.content + "\n"
                                maindata.successrate[year-2022][month - 1][day] += 1
                            }
                        }
                    }
                }
            }

            if(refresh) MainActivity.getInstance()?.refresh()

        }, {
            CustomToast.makeText(
                this,
                "모든 목록을 불러오는데 실패하였습니다.",
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    fun refresh(){
        finish()
        overridePendingTransition(0,0)
        val intent: Intent = getIntent()
        startActivity(intent)
        overridePendingTransition(0,0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getData()

        bottom_nav.setOnNavigationItemSelectedListener(onBottomNavItemSelectedListener)
        bottom_nav.menu.getItem(2).isChecked = true
        // TodoFragment를 가장 먼저 실행함
        snsFragment = SnsFragment.newInstance()
        todoFragment = TodoFragment.instance
        supportFragmentManager.beginTransaction().add(R.id.fragments_frame, todoFragment!!).commit()
    }



    //바텀 네비게이션 아이템 클릭 리스너
    private val onBottomNavItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {
            // 현재 SNS가 열려있고, 다시 sns 버튼을 누른경우 스크롤을 맨위로 보낸다.
            if(it.itemId == R.id.menu_home && binding?.fragmentSns?.visibility == View.VISIBLE){
                snsFragment?.postScrolltoTop()
            }

            /*
            데이터를 유지하기 위해
            snsFragment를 관리하는 FrameLayout이 따로 있고 해당 레이아웃만 보여지는 상태를 토글로 관리하여
            데이터를 유지시키고 다른 것들은 기존 방법을 유지하여 화면 전환을 한다.
            */
            when (it.itemId) {
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_sns, snsFragment!!).commit()
                    binding?.fragmentSns?.visibility = View.VISIBLE
                    binding?.fragmentsFrame?.visibility = View.GONE

//                    getInfo 테스트용
//                    AuthRetrofitManager.instance.getInfo(completion = { responseStatus, jsonElement ->
//                        when(responseStatus){
//                            RESPONSE_STATUS.OKAY ->{
//                                CustomToast.makeText(this, "${jsonElement}", Toast.LENGTH_SHORT).show()
//                            }
//                            RESPONSE_STATUS.FAIL ->{
//                                CustomToast.makeText(this, "${jsonElement}", Toast.LENGTH_SHORT).show()
//                            }
//                            RESPONSE_STATUS.NO_CONTENT -> {
//
//                                CustomToast.makeText(this, "${jsonElement}", Toast.LENGTH_LONG).show()
//                            }
//                        }
//                    })
                }
                R.id.menu_calendar -> {
                    calendarFragment = CalendarFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, calendarFragment!!).commit()
                    binding?.fragmentSns?.visibility = View.GONE
                    binding?.fragmentsFrame?.visibility = View.VISIBLE
                }
                R.id.menu_todo -> {
                    todoFragment = TodoFragment.instance
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, todoFragment!!).commit()
                    binding?.fragmentSns?.visibility = View.GONE
                    binding?.fragmentsFrame?.visibility = View.VISIBLE
                }
                R.id.menu_statistic -> {
                    statisticsFragment = StatisticsFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, statisticsFragment!!).commit()
                    binding?.fragmentSns?.visibility = View.GONE
                    binding?.fragmentsFrame?.visibility = View.VISIBLE
                }
                R.id.menu_etc -> {
                    etcFragment = EtcFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, etcFragment!!).commit()
                    binding?.fragmentSns?.visibility = View.GONE
                    binding?.fragmentsFrame?.visibility = View.VISIBLE
                }
            }

            true
        }
}