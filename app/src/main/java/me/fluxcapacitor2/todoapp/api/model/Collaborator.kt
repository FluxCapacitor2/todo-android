package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Collaborator(
    val id: String,
    val userId: String,
    val user: User?,
    val projectId: String,
    /**
     * `EDITOR` or `VIEWER`
     */
    val role: String
)