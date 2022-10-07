package com.example.harudemo

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.harudemo.fragments.*
import com.example.harudemo.fragments.SnsFragment.Companion.TAG
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var snsFragment: SnsFragment? = null
    private var calendarFragment: CalendarFragment? = null
    private var todoFragment: TodoFragment? = null
    private var statisticsFragment: StatisticsFragment? = null
    private var etcFragment: EtcFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav.setOnNavigationItemSelectedListener(onBottomNavItemSelectedListener)
        snsFragment = SnsFragment.newInstance()
        //todoFragment를 맨 처음 실행함
        supportFragmentManager.beginTransaction().add(R.id.fragments_frame, snsFragment!!).commit()
    }

    //바텀 네비게이션 아이템 클릭 리스너
    private val onBottomNavItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {
            /*
            데이터를 유지하기 위해
            최초 선택 시에만 프래그먼트를 만들고 이후에는 생성된 프래그먼트를 재사용한다
            현재 선택된 프래그먼트를 show하고, 나머지 프래그먼트를 hide한다.
            */
            when (it.itemId) {
                R.id.menu_home -> { //sns 메뉴 선택 시
                    if (snsFragment == null) {
                        snsFragment = SnsFragment.newInstance()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragments_frame, snsFragment!!).commit()
                    }
                    if (snsFragment != null) supportFragmentManager.beginTransaction()
                        .show(snsFragment!!).commit()
                    if (calendarFragment != null) supportFragmentManager.beginTransaction()
                        .hide(calendarFragment!!).commit()
                    if (todoFragment != null) supportFragmentManager.beginTransaction()
                        .hide(todoFragment!!).commit()
                    if (statisticsFragment != null) supportFragmentManager.beginTransaction()
                        .hide(statisticsFragment!!).commit()
                    if (etcFragment != null) supportFragmentManager.beginTransaction()
                        .hide(etcFragment!!).commit()
                }
                R.id.menu_calendar -> { //calendar 메뉴 선택 시
                    if (calendarFragment == null) {
                        calendarFragment = CalendarFragment.newInstance()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragments_frame, calendarFragment!!).commit()
                    }
                    if (calendarFragment != null) supportFragmentManager.beginTransaction()
                        .show(calendarFragment!!).commit()
                    if (snsFragment != null) supportFragmentManager.beginTransaction()
                        .hide(snsFragment!!).commit()
                    if (todoFragment != null) supportFragmentManager.beginTransaction()
                        .hide(todoFragment!!).commit()
                    if (statisticsFragment != null) supportFragmentManager.beginTransaction()
                        .hide(statisticsFragment!!).commit()
                    if (etcFragment != null) supportFragmentManager.beginTransaction()
                        .hide(etcFragment!!).commit()
                }
                R.id.menu_todo -> { // 투두 메뉴 선택시
                    if (todoFragment == null) {
                        todoFragment = TodoFragment.newInstance()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragments_frame, todoFragment!!).commit()
                    }
                    if (todoFragment != null) supportFragmentManager.beginTransaction()
                        .show(todoFragment!!).commit()
                    if (snsFragment != null) supportFragmentManager.beginTransaction()
                        .hide(snsFragment!!).commit()
                    if (calendarFragment != null) supportFragmentManager.beginTransaction()
                        .hide(calendarFragment!!).commit()
                    if (statisticsFragment != null) supportFragmentManager.beginTransaction()
                        .hide(statisticsFragment!!).commit()
                    if (etcFragment != null) supportFragmentManager.beginTransaction()
                        .hide(etcFragment!!).commit()

                }
                R.id.menu_statistic -> { // 기록 메뉴 선택 시
                    if (statisticsFragment == null) {
                        statisticsFragment = StatisticsFragment.newInstance()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragments_frame, statisticsFragment!!).commit()
                    }
                    if (statisticsFragment != null) supportFragmentManager.beginTransaction()
                        .show(statisticsFragment!!).commit()
                    if (snsFragment != null) supportFragmentManager.beginTransaction()
                        .hide(snsFragment!!).commit()
                    if (calendarFragment != null) supportFragmentManager.beginTransaction()
                        .hide(calendarFragment!!).commit()
                    if (todoFragment != null) supportFragmentManager.beginTransaction()
                        .hide(todoFragment!!).commit()
                    if (etcFragment != null) supportFragmentManager.beginTransaction()
                        .hide(etcFragment!!).commit()
                }
                R.id.menu_etc -> { // 더보기 메뉴 선택 시
                    if (etcFragment == null) {
                        etcFragment = EtcFragment.newInstance()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragments_frame, etcFragment!!).commit()
                    }
                    if (etcFragment != null) supportFragmentManager.beginTransaction()
                        .show(etcFragment!!).commit()
                    if (snsFragment != null) supportFragmentManager.beginTransaction()
                        .hide(snsFragment!!).commit()
                    if (calendarFragment != null) supportFragmentManager.beginTransaction()
                        .hide(calendarFragment!!).commit()
                    if (todoFragment != null) supportFragmentManager.beginTransaction()
                        .hide(todoFragment!!).commit()
                    if (statisticsFragment != null) supportFragmentManager.beginTransaction()
                        .hide(statisticsFragment!!).commit()
                }
            }
            true
        }


}