package me.fluxcapacitor2.todoapp.api.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import me.fluxcapacitor2.todoapp.api.db
import me.fluxcapacitor2.todoapp.api.model.DbProjectDetail
import me.fluxcapacitor2.todoapp.api.model.DbSection
import me.fluxcapacitor2.todoapp.api.model.ProjectDetail
import me.fluxcapacitor2.todoapp.api.model.Section

@Dao
interface ProjectDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRaw(vararg projects: DbProjectDetail)

    @Query("SELECT * FROM DbProjectDetail WHERE `id` = :id")
    suspend fun getRaw(id: String): DbProjectDetail?

    @Query("DELETE FROM DbProjectDetail WHERE `id` = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM DbProjectDetail")
    suspend fun deleteAll()

    @Transaction
    suspend fun insertAll(vararg projects: ProjectDetail) {
        val tasks = projects.flatMap { project ->
            project.sections.flatMap { section ->
                section.tasks
            }
        }
        db.taskDao().insertAll(*tasks.toTypedArray())
        val sections = projects.flatMap { it.sections }
        db.dbSectionDao()
            .insert(*sections.map { DbSection(it.id, it.name, it.projectId) }.toTypedArray())

        insertAllRaw(*projects.map {
            DbProjectDetail(
                it.id, it.name, it.ownerId, it.owner
            )
        }.toTypedArray())
    }

    @Transaction
    suspend fun get(id: String): ProjectDetail? {
        val raw = getRaw(id) ?: return null

        val sections = db.dbSectionDao().getAllInProject(id)
            .map { (section, tasks) -> Section(section.id, section.name, section.projectId, tasks) }

        return ProjectDetail(
            id = raw.id,
            name = raw.name,
            sections = sections,
            ownerId = raw.ownerId,
            owner = raw.owner
        )
    }
}