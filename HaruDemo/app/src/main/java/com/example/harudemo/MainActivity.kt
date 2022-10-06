package com.example.harudemo

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.harudemo.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(){
    private lateinit var snsFragment: SnsFragment
    private lateinit var calendarFragment: CalendarFragment
    private lateinit var todoFragment: TodoFragment
    private lateinit var statisticsFragment: StatisticsFragment
    private lateinit var etcFragment: EtcFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav.setOnNavigationItemSelectedListener(onBottomNavItemSelectedListener)

        snsFragment = SnsFragment.newInstance()
        //todoFragment를 맨 처음 실행함
        supportFragmentManager.beginTransaction().add(R.id.fragments_frame, snsFragment).commit()
    }

    //바텀 네비게이션 아이템 클릭 리스너
    private val onBottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when(it.itemId){
            R.id.menu_home -> {
                snsFragment = SnsFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, snsFragment).commit()
            }
            R.id.menu_calendar -> {
                calendarFragment = CalendarFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, calendarFragment).commit()
            }
            R.id.menu_todo->{
                todoFragment = TodoFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, todoFragment).commit()
            }
            R.id.menu_statistic -> {
                statisticsFragment = StatisticsFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, statisticsFragment).commit()
            }
            R.id.menu_etc->{
                etcFragment = EtcFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, etcFragment).commit()
            }
        }
        true
    }



}