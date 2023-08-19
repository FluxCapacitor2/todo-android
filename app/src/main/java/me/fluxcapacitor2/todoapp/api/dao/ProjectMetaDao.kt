package me.fluxcapacitor2.todoapp.api.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.fluxcapacitor2.todoapp.api.model.ProjectMeta

@Dao
interface ProjectMetaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg projects: ProjectMeta)

    @Query("SELECT * FROM ProjectMeta")
    suspend fun getAll(): List<ProjectMeta>

    @Delete
    suspend fun delete(projectMeta: ProjectMeta)

    @Query("DELETE FROM ProjectMeta")
    suspend fun deleteAll()
}