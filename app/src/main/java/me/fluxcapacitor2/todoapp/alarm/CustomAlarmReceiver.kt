package me.fluxcapacitor2.todoapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import me.fluxcapacitor2.todoapp.R

/**
 * Receives intents from the [CustomAlarmManager] and sends reminder notifications.
 */
class CustomAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("taskId")
        val taskProjectId = intent.getStringExtra("taskProjectId")
        val taskName = intent.getStringExtra("taskName")
        val taskDescription = intent.getStringExtra("taskDescription")
        val timeMillis = intent.getLongExtra("timeMillis", 0)

        val notification = NotificationCompat.Builder(context, "reminder_project_$taskProjectId")
            .setContentTitle(taskName)
            .setContentText(taskDescription)
            .setWhen(timeMillis)
            .setSmallIcon(R.drawable.baseline_check_circle_24)
            .build()

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w("CustomAlarmReceiver", "Can't send reminder: notification permission not granted")
        } else {
            NotificationManagerCompat.from(context).notify(taskId.hashCode(), notification)
        }
    }
}