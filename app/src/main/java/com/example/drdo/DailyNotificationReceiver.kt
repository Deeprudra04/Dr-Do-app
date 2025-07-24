package com.example.drdo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlarmManager
import android.util.Log

class DailyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DailyNotificationReceiver", "Alarm received! Showing notification.")
        val title = intent.getStringExtra("title") ?: "Task"
        val deadline = intent.getStringExtra("deadline") ?: ""
        val deadlineMillis = intent.getLongExtra("deadlineMillis", 0L)
        val channelId = "task_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Task Notifications", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = 0xFF19C900.toInt() // green
            channel.enableVibration(true)
            manager.createNotificationChannel(channel)
        }
        val manageIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("navigateTo", "manage_works")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, manageIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Dr. DO: $title")
            .setContentText("Deadline: $deadline")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setColor(0xFF19C900.toInt())
            .setStyle(NotificationCompat.BigTextStyle().bigText("Deadline: $deadline"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        manager.notify(title.hashCode(), notification)

        // Reschedule for next day if before deadline
        val now = System.currentTimeMillis()
        if (now < deadlineMillis) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = now
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val nextIntent = Intent(context, DailyNotificationReceiver::class.java).apply {
                putExtra("title", title)
                putExtra("deadline", deadline)
                putExtra("deadlineMillis", deadlineMillis)
            }
            val nextPendingIntent = PendingIntent.getBroadcast(
                context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                nextPendingIntent
            )
        }
    }
} 