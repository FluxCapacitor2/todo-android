package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reminder(
    val id: Int,
    val taskId: Int,
    val projectId: String,
    val time: String,
    @SerialName("Task")
    val task: ReminderTaskInfo
)

@Serializable
data class ReminderTaskInfo(
    val name: String,
    val dueDate: String,
    val completed: Boolean
)