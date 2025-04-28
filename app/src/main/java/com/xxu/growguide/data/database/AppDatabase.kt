package com.xxu.growguide.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xxu.growguide.data.entity.PlantsEntity
import com.xxu.growguide.data.entity.UserEntity
import com.xxu.growguide.data.entity.UserPlantsEntity
import com.xxu.growguide.data.entity.WeatherEntity

/**
 * Purpose: Room database for the GrowGuide application
 */
@Database(
    entities = [
        WeatherEntity::class,
        PlantsEntity::class,
        UserEntity::class,
        UserPlantsEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
    abstract fun plantsDao(): PlantsDao
    abstract fun userDao(): UserDao
    abstract fun userPlantsDao(): UserPlantsDao

    // Companion object implements Singleton Pattern
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

//        val MIGRATION_5_6 = object : Migration(5, 6) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                // Drop the old index with unique constraint
//                db.execSQL("DROP INDEX IF EXISTS index_user_plants_plantId_userId")
//
//                // Recreate the index without the unique constraint
//                db.execSQL("CREATE INDEX IF NOT EXISTS index_user_plants_plantId_userId ON user_plants(plantId, userId)")
//            }
//        }

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
                    .fallbackToDestructiveMigration() // This will destroy and recreate db if no migrations defined
                    //.addMigrations(MIGRATION_5_6)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}