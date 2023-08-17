package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationToken(
    val id: String,
    val token: String,
    val generatedAt: String,
    val userId: String
)