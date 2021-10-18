package com.demo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.demo.data.local.dao.MenusDao
import com.demo.data.local.dao.RestaurantsDao
import com.demo.model.Converters
import com.demo.model.MenuConverters
import com.demo.model.MenuEntity
import com.demo.model.RestaurantEntity

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
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).addMigrations(*DatabaseMigrations.MIGRATIONS).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
