package me.fluxcapacitor2.todoapp.api.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class User(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val emailVerified: Boolean?,
    val image: String?
)