package com.example.moodtracker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class HabitNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitTitle = intent.getStringExtra("HABIT_TITLE") ?: "Habit Reminder"
        //SETUP NYAAH
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt() // ID unik biar notif ga numpuk
        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // DESAINNYAAH
        val notification = NotificationCompat.Builder(context, "habit_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time for your habit!")
            .setContentText("Don't forget to: $habitTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}