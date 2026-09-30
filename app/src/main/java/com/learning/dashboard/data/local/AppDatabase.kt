package com.learning.dashboard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.learning.dashboard.data.local.dao.CourseDao
import com.learning.dashboard.data.local.dao.LessonDao
import com.learning.dashboard.data.local.entity.CourseEntity
import com.learning.dashboard.data.local.entity.LessonEntity

@Database(
    entities = [
        CourseEntity::class,
        LessonEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun lessonDao(): LessonDao

    companion object {
        private const val DATABASE_NAME = "learning_dashboard.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
