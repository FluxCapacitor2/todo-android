package me.fluxcapacitor2.todoapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectDetail(
    val id: String,
    val name: String,
    val ownerId: String,
    val sections: Array<Section>,
    val owner: User
)