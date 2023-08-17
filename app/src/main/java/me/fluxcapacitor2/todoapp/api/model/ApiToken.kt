package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiToken(
    val id: String,
    val generatedAt: String,
    val userId: String
)