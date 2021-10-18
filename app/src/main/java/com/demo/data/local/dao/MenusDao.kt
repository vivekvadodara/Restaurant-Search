package com.demo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.model.MenuEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(items: List<MenuEntity>)

    @Query("SELECT * FROM ${MenuEntity.MENU_TABLE_NAME}")
    fun getAll(): Flow<List<MenuEntity>>
}
