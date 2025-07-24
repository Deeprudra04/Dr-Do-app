package com.example.drdo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.text.SimpleDateFormat
import java.util.*
import android.app.PendingIntent
import android.content.Intent

class DailyNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.failure()
        val deadline = inputData.getString("deadline") ?: ""
        val deadlineMillis = inputData.getLong("deadlineMillis", 0L)
        val now = System.currentTimeMillis()
        if (now > deadlineMillis) {
            // Deadline passed, don't show notification
            return Result.success()
        }
        showNotification(applicationContext, title, deadline)
        return Result.success()
    }

    private fun showNotification(context: Context, title: String, deadline: String) {
        val channelId = "task_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Task Notifications", NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = 0xFF19C900.toInt() // green
            channel.enableVibration(true)
            manager.createNotificationChannel(channel)
        }
        // Intent to open Manage Works screen
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("navigateTo", "manage_works")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Dr. DO: $title")
            .setContentText("Deadline: $deadline")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setColor(0xFF19C900.toInt()) // green
            .setStyle(NotificationCompat.BigTextStyle().bigText("Deadline: $deadline"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        manager.notify(title.hashCode(), notification)
    }
} 