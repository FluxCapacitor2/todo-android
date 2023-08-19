package me.fluxcapacitor2.todoapp.api.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import me.fluxcapacitor2.todoapp.api.model.DbSection
import me.fluxcapacitor2.todoapp.api.model.DbSectionWithTasks

@Dao
interface DbSectionDao {

    @Query("SELECT * FROM DbSection WHERE `id` = :id")
    suspend fun getRaw(id: Int): DbSection

    @Transaction
    @Query("SELECT * FROM DbSection WHERE `id` IN (:ids)")
    suspend fun getAll(ids: List<Int>): List<DbSectionWithTasks>

    @Transaction
    @Query("SELECT * FROM DbSection WHERE `projectId` = :projectId")
    suspend fun getAllInProject(projectId: String): List<DbSectionWithTasks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg sections: DbSection)
}