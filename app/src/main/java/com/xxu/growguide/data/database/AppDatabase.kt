package com.xxu.growguide.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xxu.growguide.data.entity.PlantsEntity
import com.xxu.growguide.data.entity.UserEntity
import com.xxu.growguide.data.entity.WeatherEntity

/**
 * Purpose: Room database for the GrowGuide application
 */
@Database(
    entities = [WeatherEntity::class, PlantsEntity::class, UserEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
    abstract fun plantsDao(): PlantsDao
    abstract fun userDao(): UserDao

    // Companion object implements Singleton Pattern
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Purpose: Get the singleton instance of the Room database
         *
         * @param context Application context used to initialize the database
         * @return The AppDatabase instance
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "growguide_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}