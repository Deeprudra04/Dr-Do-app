package com.example.drdo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.drdo.data.Task
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun scheduleDailyNotification(context: Context, task: Task, workTimeHour: Int, workTimeMinute: Int) {
    val workRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(1, TimeUnit.DAYS)
        .setInputData(
            workDataOf(
                "title" to task.title,
                "deadline" to SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(task.dateTime)),
                "deadlineMillis" to task.dateTime
            )
        )
        .setInitialDelay(calculateInitialDelay(workTimeHour, workTimeMinute), TimeUnit.MILLISECONDS)
        .addTag("task_daily_${task.id}")
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "task_daily_${task.id}",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}

fun calculateInitialDelay(hour: Int, minute: Int): Long {
    val now = Calendar.getInstance()
    val next = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
    }
    return next.timeInMillis - now.timeInMillis
}

fun scheduleDeadlineAlarm(context: Context, task: Task) {
    val intent = Intent(context, DeadlineAlarmReceiver::class.java).apply {
        putExtra("title", task.title)
        putExtra("deadline", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(task.dateTime)))
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, task.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        task.dateTime,
        pendingIntent
    )
}

fun scheduleDailyExactNotification(context: Context, task: Task, workTimeHour: Int, workTimeMinute: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, workTimeHour)
        set(Calendar.MINUTE, workTimeMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis < System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    val intent = Intent(context, DailyNotificationReceiver::class.java).apply {
        putExtra("title", task.title)
        putExtra("deadline", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(task.dateTime)))
        putExtra("deadlineMillis", task.dateTime)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, (task.id * 1000).toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}

fun cancelDailyExactNotification(context: Context, task: Task) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, DailyNotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, (task.id * 1000).toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
} 