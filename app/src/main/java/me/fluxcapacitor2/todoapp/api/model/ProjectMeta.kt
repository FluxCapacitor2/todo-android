package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectMeta(val id: String, val name: String, val ownerId: String)