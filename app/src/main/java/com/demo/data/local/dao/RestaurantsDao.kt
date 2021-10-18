package com.demo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.model.RestaurantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(posts: List<RestaurantEntity>)

    @Query("SELECT * FROM ${RestaurantEntity.RESTAURANT_TABLE_NAME}")
    fun getAll(): Flow<List<RestaurantEntity>>
}
