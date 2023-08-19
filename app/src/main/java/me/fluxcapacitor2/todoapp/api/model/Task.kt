package me.fluxcapacitor2.todoapp.api.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Task(
    @PrimaryKey
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
    val projectId: String
)