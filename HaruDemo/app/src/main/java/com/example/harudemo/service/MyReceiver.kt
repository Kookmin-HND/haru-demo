package com.example.harudemo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.harudemo.R
import com.example.harudemo.MainActivity
import com.example.harudemo.fragments.maindata
import com.example.harudemo.service.Constant.Companion.CHANNEL_ID
import com.example.harudemo.service.Constant.Companion.NOTIFICATION_ID
import com.example.harudemo.todo.TodoData
import java.util.*

class MyReceiver : BroadcastReceiver() {
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        print("알람 받음")
        Log.d("알람","알람 받음")
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        deliverNotification(context)
    }

    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, // 채널의 아이디
                "채널 이름입니다.", // 채널의 이름
                NotificationManager.IMPORTANCE_HIGH
                /*
                1. IMPORTANCE_HIGH = 알림음이 울리고 헤드업 알림으로 표시
                2. IMPORTANCE_DEFAULT = 알림음 울림
                3. IMPORTANCE_LOW = 알림음 없음
                4. IMPORTANCE_MIN = 알림음 없고 상태줄 표시 X
                 */
            )

//            var sectiondata = TodoData.getTodos()

//            for (section in sectiondata){
//                for(todo in section.todoList){
//                    var content = todo.content
//                    var date = todo.date
//                    var splitdate = date.split("-")
//                    val year = splitdate[0].toInt()
//                    val month = splitdate[1].toInt()
//                    val day = splitdate[2].toInt()
//                    maindata.contents[year-2022][month-1][day] += content+"\n"
//                }
//            }

            var calendar = Calendar.getInstance()

            val y = calendar.get(Calendar.YEAR)
            val m = calendar.get(Calendar.MONTH)
            val d = calendar.get(Calendar.DAY_OF_MONTH)

            notificationChannel.enableLights(true) // 불빛
            notificationChannel.lightColor = Color.RED // 색상
            notificationChannel.enableVibration(true) // 진동 여부
            notificationChannel.description = maindata.contents[y-2022][m-1][d] //채널정보
            
            notificationManager.createNotificationChannel(
                notificationChannel)
        }
    }

    private fun deliverNotification(context: Context){
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID, // requestCode
            contentIntent, // 알림 클릭 시 이동할 인텐트
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // 아이콘
            .setContentTitle("하루 알람") // 제목
            .setContentText("오늘의 일정입니다.") // 내용
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}