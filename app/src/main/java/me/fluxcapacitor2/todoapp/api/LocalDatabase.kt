package me.fluxcapacitor2.todoapp.api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.fluxcapacitor2.todoapp.api.dao.DbSectionDao
import me.fluxcapacitor2.todoapp.api.dao.ProjectDetailDao
import me.fluxcapacitor2.todoapp.api.dao.ProjectMetaDao
import me.fluxcapacitor2.todoapp.api.dao.TaskDao
import me.fluxcapacitor2.todoapp.api.model.DbProjectDetail
import me.fluxcapacitor2.todoapp.api.model.DbSection
import me.fluxcapacitor2.todoapp.api.model.ProjectMeta
import me.fluxcapacitor2.todoapp.api.model.Task
import me.fluxcapacitor2.todoapp.api.model.User

lateinit var db: LocalDatabase

fun initializeDatabase(context: Context) {
    if (::db.isInitialized) {
        return
    }
    db = Room.databaseBuilder(
        context.applicationContext,
        LocalDatabase::class.java, "todo-app"
    )
        .fallbackToDestructiveMigration()
        .build()
}

@Database(
    entities = [ProjectMeta::class, DbProjectDetail::class, User::class, DbSection::class, Task::class],
    version = 2
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun projectMetaDao(): ProjectMetaDao
    abstract fun projectDetailDao(): ProjectDetailDao
    abstract fun dbSectionDao(): DbSectionDao
    abstract fun taskDao(): TaskDao
}
