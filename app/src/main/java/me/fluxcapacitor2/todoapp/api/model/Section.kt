package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Section(
    val id: Int, val name: String, val projectId: String, val tasks: Array<Task>
)