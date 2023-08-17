package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val emailVerified: Boolean?,
    val image: String?
)