package com.example.notialarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReciever : BroadcastReceiver() {

    companion object{
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }
    override fun onReceive(context: Context, intent: Intent?) {// 실제로 브로드캐스트에 pendingintent가 수신이 되었을 때 오는 콜백함수
        //노티를 띄움
        //Notification 채널 필요

        createNotificationChannel(context)
        notifyNotification(context)


    }
    private fun createNotificationChannel(context : Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ //버전이 된다면 채널 만들어줌
            val notificationChannel = NotificationChannel(
                "1000",
                "기상 알람",
                NotificationManager.IMPORTANCE_HIGH
            )

            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)//매니저를 통해 채널을 넣어줌으로서 만들어줌
        }
    }

    private fun notifyNotification(context : Context){
        //with스코프 함수 //매니저를 가져오고
        with(NotificationManagerCompat.from(context)){
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("알람")
                .setContentText("일어날 시간입니다.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notify(NOTIFICATION_ID,build.build())
        }
    }

}