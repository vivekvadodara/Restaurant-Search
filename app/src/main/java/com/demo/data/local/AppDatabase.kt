package com.demo.data.local

import android.R
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.demo.data.local.dao.MenusDao
import com.demo.data.local.dao.RestaurantsDao
import com.demo.data.repository.RestaurantRepositoryImpl
import com.demo.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.io.InputStream

@Database(
    entities = [RestaurantEntity::class, MenuEntity::class],
    version = DatabaseMigrations.DB_VERSION
)
@TypeConverters(Converters::class, MenuConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getRestaurantDao(): RestaurantsDao

    abstract fun getMenuDao(): MenusDao

    companion object {
        const val DB_NAME = "my_database"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
                                .build()
                            WorkManager.getInstance(context).enqueue(request)
                        }
                    }
                )
                .addMigrations(*DatabaseMigrations.MIGRATIONS)
                .build()
        }
    }
}
