package me.fluxcapacitor2.todoapp.alarm

import android.app.NotificationChannel
import android.content.Context
import androidx.core.app.NotificationManagerCompat
object NotificationSetup {

    fun registerChannel(context: Context, id: String, name: String, importance: Int) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(NotificationChannel(id, name, importance))
    }


}