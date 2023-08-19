package me.fluxcapacitor2.todoapp.api.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Entity
data class DbProjectDetail(
    @PrimaryKey
    val id: String,
    val name: String,
    val ownerId: String,
    @Embedded(prefix = "owner_") val owner: User
)

@Serializable
data class ProjectDetail(
    val id: String,
    val name: String,
    val ownerId: String,
    val sections: List<Section>,
    val owner: User
)
