package me.fluxcapacitor2.todoapp.api.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Entity
data class DbSection(
    @PrimaryKey
    val id: Int, val name: String, val projectId: String
)

data class DbSectionWithTasks(
    @Embedded val section: DbSection,
    @Relation(
        parentColumn = "id",
        entityColumn = "sectionId"
    )
    val tasks: List<Task>
)

@Serializable
data class Section(
    val id: Int,
    val name: String,
    val projectId: String,
    val tasks: List<Task>
)