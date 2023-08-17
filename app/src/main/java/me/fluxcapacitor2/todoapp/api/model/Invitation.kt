package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Invitation(
    val id: String,
    val senderId: String,
    val from: User?,
    val receiverId: String,
    val to: User?,
    val projectId: String,
    val project: ProjectMeta
)