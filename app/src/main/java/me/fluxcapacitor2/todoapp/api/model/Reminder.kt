package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Reminder(
    val id: Int,
    val taskId: Int,
    val projectId: String,
    val time: String
)