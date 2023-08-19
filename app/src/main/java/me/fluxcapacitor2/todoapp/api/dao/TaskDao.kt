package me.fluxcapacitor2.todoapp.api.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.fluxcapacitor2.todoapp.api.model.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg tasks: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM Task")
    suspend fun deleteAll()
}