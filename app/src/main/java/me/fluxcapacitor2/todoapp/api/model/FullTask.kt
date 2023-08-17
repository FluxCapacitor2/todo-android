package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class FullTask(
    val id: Int,
    val name: String,
    val description: String,
    val priority: Int,
    val createdAt: String,
    val updatedAt: String,
    val completed: Boolean,
    val startDate: String?,
    val dueDate: String?,
    val sectionId: Int?,
    val parentTaskId: Int?,
    val ownerId: String,
    val projectId: String,
    val subTasks: Array<Task>
)