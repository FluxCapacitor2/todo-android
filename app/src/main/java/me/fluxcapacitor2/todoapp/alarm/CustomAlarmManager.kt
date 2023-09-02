package me.fluxcapacitor2.todoapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class CustomAlarmManager(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Returns a PendingIntent that will send the reminder when started.
     */
    private fun getPendingIntent(
        taskId: String,
        taskProjectId: String,
        taskName: String,
        taskDescription: String,
        timeMillis: Long
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, CustomAlarmReceiver::class.java)
                .putExtra("taskId", taskId)
                .putExtra("taskProjectId", taskProjectId)
                .putExtra("taskName", taskName)
                .putExtra("taskDescription", taskDescription)
                .putExtra("timeMillis", timeMillis)
                .setAction("notify"),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val scheduledIntents = hashMapOf<String, PendingIntent>()

    /**
     * Schedules a reminder for the task with the given ID, project, details, and time.
     * If a reminder is scheduled with the same ID as an already scheduled task,
     * the first one will be cancelled before the new one is scheduled.
     */
    fun schedule(
        taskId: String,
        taskProjectId: String,
        taskName: String,
        taskDescription: String,
        timeMillis: Long
    ) {
        if (scheduledIntents.contains(taskId)) cancel(taskId)
        val intent = getPendingIntent(taskId, taskProjectId, taskName, taskDescription, timeMillis)
        scheduledIntents[taskId] = intent
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeMillis,
                    intent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeMillis,
                intent
            )
        }
    }

    fun cancel(taskId: String) {
        scheduledIntents[taskId]?.let { alarmManager.cancel(it) }
        scheduledIntents.remove(taskId)
    }

}